### 23.11.2016 [*v0.02.02*]

 - Improved readability and functionality of code in TimerWidgetView
 - Simplified use of Configurable interface in PropertyConfigActivity
 - Fixed bug in PropertyConfigActivity that would cause every config window to be a prefab config window
 - Fixed clicking functionality in notifications

### 22.11.2016 [*v0.02.01*]

 - Improved the appearance of widgets further (modified radial gradient)
 - Adjusted amount to fade inactive widgets
 - Adjusted brightness threshold for selecting contrast color (white/black) for text and borders in views
 - Fixed bug with rendering gradients in active items in config activity
 - Adjusted widget refresh rate calculation to disregard expired timers
 - Improved alarm notification:
  - Added button to dismiss alarm that is accessible in lock screen without unlocking
  - Made notification persistent until alarm is stopped
  - Forced screen to turn on when alarm is going off

### 21.11.2016 [*v0.01.03*]

 - Moved color manipulating methods into new ColorUtils class
 - Improved appearance of widgets (added radial gradient and indicator shadow)

### 20.11.2016 [*v0.01.02*]

 - Completed documentation/commenting
 - Modified Prefab and ActiveItem to implement new 'Configurable' interface, merging shared methods
 - Utilized new Configurable interface to simplify methods in PropertyConfigActivity
 - Refactored some method names in Configurable to be more descriptive
 - Refactored ColorPickerView to ColorSwitcherView to avoid conflict with library class
 - Modified ColorSwitcherView to allow selection of a specific color
 
### 19.11.2016 [*v0.01.01*]
 
 - Initial release
