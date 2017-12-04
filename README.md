# GT Interactive
An Android mobile application that allows the user to explore places, events, news, organizations, parking, and more on Georgia Tech (GT) campus in an interactive map format.

## Release Notes for GT Interactive 1.0

Dec 4, 2017

### New Features:
- Users can browse an interactive map of the GT campus and click on map objects (e.g. places and events) to view detailed information.
- Users can view a map of parking locations and printer locations on GT campus.
- Users can view a real-time traffic overlay on GT campus.
- Users can search for places and events.
- Users can browse places and events in list format and filter by category.

### Known Issues:
- Switching between views on the main map resets camera position.
- Map markers overlap for events taking place at the same location.
- Place name in place details page currently gets cut off if too long.
- Events tab in place details page currently displays all events rather than only those belonging to the place.

### Missing Features:
- Ability to filter places and events by category within the main map activity.
- Ability to see past and future events (can only see today's events).
- Ability to automatically sync data with REST API. The user must manually click "reload" in Places Test and Events Test activities to fetch new data from the server.
- Splash screen
    
## Install Guide

1. Download and install [Android Studio](https://developer.android.com/studio/index.html) if you haven't already.

2. Clone this repository to your local machine.

    ```
    git clone https://github.com/vnguyen70/GTInteractive.git
    ```

3. Open Android Studio and import this project. Follow any prompts for syncing gradle build files and installing any dependencies.

4. Click "Run" in the toolbar to automatically build and run the app. [More info](https://developer.android.com/training/basics/firstapp/running-app.html)

## Troubleshooting

- Unable to build/run app - follow error prompts and suggestions displayed by Android Studio.
- Unable to load data while using app - verify that all [API endpoints](https://gtapp-api.rnoc.gatech.edu/docs/index.html) are accessible and properly working.
- App crashes during normal use - completely uninstall app, removing any local application files from the device (or emulator), and reinstall the app.

## Authors

- [Jonathan Hernandez](https://github.com/Jonathan-Hernandez14)
- [Rayner Kristanto](https://github.com/RaynerKristanto)
- [Vivian Lee](https://github.com/cmvivianlee)
- [Vi Nguyen](https://github.com/vnguyen70)
- [Kaliq Wang](https://github.com/kaliqwang)

## License

GT Interactive is released under the [MIT License](https://opensource.org/licenses/MIT).
