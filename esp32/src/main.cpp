#include <Arduino.h>
#include <BLE2902.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>
#include <BLECharacteristic.h>
#include <BLEService.h>

#define DEBUG 1  

#if DEBUG
  #define DEBUG_PRINT(x) Serial.print(x)
  #define DEBUG_PRINTLN(x) Serial.println(x)
#else
  #define DEBUG_PRINT(x) 
  #define DEBUG_PRINTLN(x) 
#endif

#define BUTTON_PIN          0 
#define LED_PIN             2
#define SERVICE_UUID        "12345678-1234-5678-1234-56789abcdef0"
#define BUTTON_UUID         "f3d9e507-5fbe-48c6-9f07-0173f5fae9b0"
#define RECEIVE_UUID        "d0b39442-8be6-4d15-9610-054c3efc1c4f"

bool deviceConnected = false;
bool buttonReleased = true;
bool buttonPressed = false;
static BLECharacteristic *pButtonCharacteristic = NULL;
static BLECharacteristic *pReceiveCharacteristic = NULL;

// Class to handle BLE connection/disconnection
class MyServerCallbacks : public BLEServerCallbacks 
{
    void onConnect(BLEServer* pServer) 
    {
        deviceConnected = true;
        DEBUG_PRINTLN("Device connected!");

        const char *initialMsg = "0";
        pButtonCharacteristic->setValue((uint8_t *)initialMsg, strlen(initialMsg));
        pButtonCharacteristic->notify();
        DEBUG_PRINTLN("Initial state -> value 0 sent!");
    }

    void onDisconnect(BLEServer* pServer) 
    {
        deviceConnected = false;
        DEBUG_PRINTLN("Device disconnected!");
        pServer->getAdvertising()->start();  // Restart advertising for new connections
    }
};

// Class to handle incoming BLE messages
class MyCallbacks : public BLECharacteristicCallbacks 
{
    void onWrite(BLECharacteristic *pCharacteristic) 
    {
        std::string value = pCharacteristic->getValue();
        if (value.length() > 0) 
        {
            DEBUG_PRINT("Message received: ");
            DEBUG_PRINTLN(value.c_str());

            if (value == "1") 
            {
                DEBUG_PRINTLN("Command: Turn on LED!");
                digitalWrite(LED_PIN, HIGH);
            } 
            else if (value == "0") 
            {
                DEBUG_PRINTLN("Command: Turn off LED!");
                digitalWrite(LED_PIN, LOW);
            } 
            else 
            {
                DEBUG_PRINTLN("Unknown command!");
            }
        }
    }
};

void setup() 
{
    setCpuFrequencyMhz(80);
    Serial.begin(115200);

    // Set pin modes
    pinMode(BUTTON_PIN, INPUT);
    pinMode(LED_PIN, OUTPUT);
    
    DEBUG_PRINTLN("Start");

    // Initialize BLE device
    BLEDevice::init("Ti Penso");

    // Create BLE server
    BLEServer *pServer = BLEDevice::createServer();
    if (!pServer) {
        DEBUG_PRINTLN("Failed to create BLE server!");
        return;
    }
    pServer->setCallbacks(new MyServerCallbacks());

    // Create Service
    BLEService *pService = pServer->createService(SERVICE_UUID);
    if (!pService) {
        DEBUG_PRINTLN("Failed to create BLE service!");
        return;
    }

    // Create characteristics
    pButtonCharacteristic = new BLECharacteristic(BUTTON_UUID, BLECharacteristic::PROPERTY_NOTIFY); 
    if (!pButtonCharacteristic) {
        DEBUG_PRINTLN("ButtonCharacteristic NULL");
        return;
    }
    
    pReceiveCharacteristic = new BLECharacteristic(RECEIVE_UUID, BLECharacteristic::PROPERTY_WRITE);
    if (!pReceiveCharacteristic) {
        DEBUG_PRINTLN("ReceiveCharacteristic NULL");
        return;
    }

    BLEDescriptor *pCCCD = new BLE2902();
    pButtonCharacteristic->addDescriptor(pCCCD);

    pService->addCharacteristic(pReceiveCharacteristic);
    
    pService->addCharacteristic(pButtonCharacteristic);
    
    pReceiveCharacteristic->setCallbacks(new MyCallbacks());

    // Start BLE Service
    pService->start();

    BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
    pAdvertising->addServiceUUID(SERVICE_UUID);
    
    pAdvertising->start();

    DEBUG_PRINTLN("BLE ready, waiting for connections...");
}

void loop() 
{
    if (deviceConnected)
    {
        if (digitalRead(BUTTON_PIN) == HIGH) 
        {
            if (!buttonPressed) 
            {
                buttonPressed = true;
                buttonReleased = false; 
                const char *msg = "1";
                pButtonCharacteristic->setValue((uint8_t *)msg, strlen(msg));
                pButtonCharacteristic->notify(); 
                DEBUG_PRINTLN("Button pressed -> value 1 sent!");
            }
        } 
        else 
        {
            if (!buttonReleased) 
            {
                buttonPressed = false;
                buttonReleased = true; 
                const char *msg = "0";
                pButtonCharacteristic->setValue((uint8_t *)msg, strlen(msg));
                pButtonCharacteristic->notify(); 
                DEBUG_PRINTLN("Button released -> value 0 sent!");
            }
        }
    }

    delay(100); //87 mA
    
}
