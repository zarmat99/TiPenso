# Pull Request Template

## Title
```
type(component): short description
```

## Description
```markdown
## Description
[Provide a brief description of the changes]

## Related Issue
- Closes #[issue-number]
- Addresses #[issue-number]

## Type of Change
- [ ] New feature (non-breaking change which adds functionality)
- [ ] Bug fix (non-breaking change which fixes an issue)
- [ ] Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] Documentation update

## Components Affected
- [ ] ESP32 Firmware
- [ ] Android App
- [ ] Documentation

## Testing
- [ ] Unit tests added/updated
- [ ] Manual testing performed
- [ ] Tested on real devices

### Test Cases
1. [ ] Test case 1
2. [ ] Test case 2
3. [ ] Test case 3

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review of code performed
- [ ] Documentation updated
- [ ] No new warnings or errors
- [ ] All tests passing
- [ ] Changes tested on both ESP32 and Android
- [ ] Breaking changes documented

## Screenshots (if applicable)

## Additional Notes
[Any additional information that reviewers should know]
```

## Example Pull Request

```markdown
## Description
Added new BLE characteristic for button state monitoring and implemented corresponding Android UI updates.

## Related Issue
- Closes #123
- Addresses #124

## Type of Change
- [x] New feature (non-breaking change which adds functionality)
- [ ] Bug fix (non-breaking change which fixes an issue)
- [ ] Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] Documentation update

## Components Affected
- [x] ESP32 Firmware
- [x] Android App
- [x] Documentation

## Testing
- [x] Unit tests added/updated
- [x] Manual testing performed
- [x] Tested on real devices

### Test Cases
1. [x] Button press detected and transmitted
2. [x] Android app receives and displays button state
3. [x] LED responds to button press
4. [x] Connection remains stable during testing

## Checklist
- [x] Code follows project style guidelines
- [x] Self-review of code performed
- [x] Documentation updated
- [x] No new warnings or errors
- [x] All tests passing
- [x] Changes tested on both ESP32 and Android
- [x] Breaking changes documented

## Screenshots
[Android UI showing new button state indicator]

## Additional Notes
- Requires ESP32 firmware version 1.2.0 or higher
- Android app version bump to 2.1.0
``` 