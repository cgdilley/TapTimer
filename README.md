# TapTimer 

**TapTimer** is an app designed to provide a simple means of setting alarms for a particular *duration*, as opposed to setting a particular *time*.  As a user, you may place any number of TapTimer widgets to your homescreen, and modifying their properties using the app.  You may store common timer presets as well, which can be quickly applied to any active widget.

## Installing the App

The latest APK file can always be found in the *'/LATEST_APK'* directory.  The following link will also be kept updated with the latest APK:  **[Download](https://drive.google.com/open?id=0B8zQ4O1-JvDrTUV5S2RwY2d5V2c)**

Once having downloaded this file onto your Android device, launch the file to install the app.

## Placing the Widgets

The widget for this app will be found in your widget menu (which is usually found alongside your app menu), labelled *TapTimer*.  Drag the widget onto your homescreen to place the widget.  Any number of widgets may be placed in this manner.

Placing a widget will open the configuration screen.  See instructions below for using this screen.  
This same screen may also be opened by launching the app itself, found in your app menu.  Settings for widgets may be modified at any time.  Press confirm in the screen to confirm the placement of the widget, or press cancel to cancel it's placement.

*Users may find it useful to keep the app's launch icon near the widgets on their homescreen to allow for quick editing.*

## Configuring the Widgets

Upon opening the configuration screen (either when first placing a widget, or when launching the app), a list of existing widgets will populate the leftmost column of the screen.  Pressing and holding (long-clicking) on these icons will open the property configuration window.  Here you may adjust the following settings:

 - **Name**:  The label to associate with the widget **(currently not displayed anywhere)*.
 - **Icon**:  The emoji icon to display on the widget.  Tapping this button will open an emoji selection screen.  Selecting any emoji in this screen will set the new emoji, or the screen may be closed without selecting anything by pressing the backspace icon or pressing back on the device.
 - **Color**:  The color display as the background color of the widget.  You may select any of the 4 pre-built colors, or tap the rightmost color to open a custom color window.
 - **Duration**:  The duration of the timer, split into hours, minutes, and seconds.
 - **Reset**:  Pressing this button will reset this timer, erasing any elapsed time on it.
 
Pressing confirm in this property configuration window will immediately apply all settings to the widget, even if the cancel button is then pressed in the main configuration screen.

## Using Pre-built Timers (Prefabs)

In the main configuration screen, the right-hand side of the screen is dedicated to a list of pre-built timer settings, or **prefabs**.  By default, a 'Nap' timer is included.  Additional prefabs may be added by pressing the **'+'** icon.  This will bring up the property configuration window, similar to the window as described in the above section.  

By pressing and holding (long-clicking) on existing prefab icons, this same property configuration window will pop up to allow you to edit the properties of that prefab (or remove it from the list, by pressing the *'delete'* button.

Any changes to prefabs (adding, editing, or deleting) will only be saved once the *'confirm'* button is pressed.  Pressing *'cancel'* will discard these changes.

To apply the settings of a prefab to an existing widget, select the widget in the left column (single tap), and then tap the icon of the prefab you wish to apply (also single tap).  Applying settings in this manner will immediately modify the widget, whether or not the *'confirm'* button is pressed.

## Using the Widgets

Once the widgets have been placed and configured, simply tapping the widgets on the homescreen will pause and unpause each individual widget.  All widgets behave entirely independent from one another.  Once a timer has elapsed its full duration, tapping it again will reset it to its full duration.  Any widget that has been started may be reset in the widget's property configuration window.

Widgets will update semi-infrequently, depending on the duration of the timer.  This is so that the timers do not significantly impact the performance of the device.

Upon reaching their set duration, a widget will start an alarm sound on the device, using the device's default alarm settings.  These alarms will go off whether the device is awake or not.  The alarm will continue until dismissed, either by tapping any widget, or by tapping the notification pop-up.  This notification may be used to dismiss the alarm while the device is still locked, if notifications may be shown in the lock screen.

## Features on the To-Do List

 - Provide more documentation in code
 - Add Help icons for in-app instructions
 - Improving the alarm notification by making it persist until the alarm has been dismissed, and by adding a quick action button to the notification rather than requiring the notification itself be tapped.
 - Turning the device's screen on when the alarm goes off.
  - This will require the Android *WakeLock* permission, removing the app's status of being permission-less.
 - Adding the ability to modify alarm settings (different ringtones, ringtone volume, etc.)
 - Adding the ability to import/export lists of prefabs (or even synching online)
 - Adding double-click functionality to widgets to reset them more easily
  - Due to the limited nature of Android widgets, this may not be possible/practical

 
## License

    TapTimer - A Timer Widget App
    Copyright (C) 2016 Dilley, Christopher

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.










> Written with [StackEdit](https://stackedit.io/).
