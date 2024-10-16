# notification_channel

A new Flutter project.

#### Documentation

##### Permissions 

add below to AndroidManifest.xml

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- Permissions options for the `notification` group -->
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
    
    <application
```

##### Notifications UI

Update below folders for Customizing Notification UI

```md
android\app\src\main\res\drawable

android\app\src\main\res\layout
```

##### Notification Native Functionality

Update code in below file

```md
android\app\src\main\kotlin\com\example\notification_channel\MainActivity.kt
```

That's It. 

##### Demo below


Light theme

<table>
  <tr>
    <td>
      <img src="https://github.com/user-attachments/assets/c47100d1-a1bf-4e7a-af25-58e359c978b0" alt="Light Theme Image 1" width="300"/>
    </td>
    <td>
      <img src="https://github.com/user-attachments/assets/807d72bb-f7f2-4c7a-a59f-b2688a6685ff" alt="Light Theme Image 2" width="300"/>
    </td>
  </tr>
</table>


Dark theme

<table>
  <tr>
    <td>
      <img src="https://github.com/user-attachments/assets/0b1b342a-82b1-443f-b5f3-1caf1a8bfeb5" alt="Dark Theme Image 1" width="300"/>
    </td>
    <td>
      <img src="https://github.com/user-attachments/assets/079eb7ed-d369-4a70-a017-89e5546e0977" alt="Dark Theme Image 2" width="300"/>
    </td>
  </tr>
</table>




