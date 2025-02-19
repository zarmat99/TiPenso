#include <Arduino.h>
#include <BLE2902.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>
#include <BLECharacteristic.h>
#include <BLEService.h>

#define BUTTON_PIN          0 
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
        Serial.println("Device connected!");
    }

    void onDisconnect(BLEServer* pServer) 
    {
        deviceConnected = false;
        Serial.println("Device disconnected!");
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
            Serial.print("Message received: ");
            Serial.println(value.c_str());

            if (value == "TURN_ON") 
            {
                Serial.println("Command: Turn on LED!");
                
            } 
            else if (value == "TURN_OFF") 
            {
                Serial.println("Command: Turn off LED!");
                
            } 
            else 
            {
                Serial.println("Unknown command!");
            }
        }
    }
};

void setup() 
{
    Serial.begin(115200);
    pinMode(BUTTON_PIN, INPUT);
    Serial.println("START");

    // Initialize BLE device
    BLEDevice::init("Ti Penso");

    // Create BLE server
    BLEServer *pServer = BLEDevice::createServer();
    pServer->setCallbacks(new MyServerCallbacks());

    // Create Service
    BLEService *pService = pServer->createService(SERVICE_UUID);

    // Create characteristics
    pButtonCharacteristic = new BLECharacteristic(BUTTON_UUID, BLECharacteristic::PROPERTY_NOTIFY); 
    if(pButtonCharacteristic == NULL)
    {
        Serial.println("ButtonCharacteristic NULL");
    }
    pReceiveCharacteristic = new BLECharacteristic(RECEIVE_UUID, BLECharacteristic::PROPERTY_WRITE);
    if(pReceiveCharacteristic == NULL)
    {
        Serial.println("ReceiveCharacteristic NULL");
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

    Serial.println("BLE ready, waiting for connections...");
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
                Serial.println("Button pressed -> value 1 sent!");
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
                Serial.println("Button released -> value 0 sent!");
            }
        }
    }

    delay(100);
}
