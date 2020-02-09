[![Build Status](https://dev.azure.com/mathandoro/CoachPlus/_apis/build/status/coach-plus.android?branchName=master)](https://dev.azure.com/mathandoro/CoachPlus/_build/latest?definitionId=6&branchName=master)

# android
Coach+ Android App

## Setup
1. Download google-services.json from Firebase and put it inside the android/app/ directory


## Releases
New features can be tested using the Coach+ Early Access version. Note that you can't access productive data when using Coach+ Early Access. 

## Create new Release (Owners only)
Make sure that you increment the versionCode and versionName for every release. 
Otherwise the upload to Google Play will fail. 
```
versionCode 13
versionName "1.1.4"
```

Once the build was successfull you can [trigger a new release](https://dev.azure.com/mathandoro/CoachPlus/_release?definitionId=3&view=mine&_a=releases). The app will be deployed to Coach+ (Early Access) first. Once you think it's ready to be published to production, simply  approve the pending approval in the release build.