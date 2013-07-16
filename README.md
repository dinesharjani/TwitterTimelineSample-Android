# TwitterTimelineSample-Android

## Introduction

![Screenshot](https://github.com/the7thgoldrunner/twittertimelinesample-android/raw/master/images/twittertimelinesample-android-holo.png)

Following the footsteps of my [Facebook Newsfeed Sample Code] (https://github.com/the7thgoldrunner/FacebookNewsfeedSample-Android) I made this sample using Twitter instead. However, I consider it perhaps a "worse" attempt because a couple of things didn't allow me to really make it as whole and cohesive as I'd want it to: I made use of the [Loader framework for the first time] (http://developer.android.com/guide/components/loaders.html) which renders the ListView Adapter somewhat pointless compared to a more classic design without Loaders. Also, I used [Android Studio] (http://developer.android.com/sdk/installing/studio.html) from the beginning, which means I had to use Gradle, and I could not get it to properly import [Chris Banes' Pull-to-Refresh Project] (https://github.com/chrisbanes/Android-PullToRefresh), so instead, you Refresh the Timeline via Menu/ActionBar.

## Description

This simple Project is targeted at Android 2.3 and upwards, and uses the Twitter4j to handle most of the proceedings with Twitter itself. It also makes use of a library to handle downloading asynchronously every user's profile picture, and as I've mentioned, Refreshing is done via menu because Gradle and I are not friends yet.

For testing you start the app, wait for the link to appear, tap on it, get Twitter's PIN code number, get back to the app, type it in and press 'OK'. Once that's done, you get your Timeline. You should also watch out because even if Refreshing keeps the Timeline intact, the API provides no more than 20 tweets as standard, so if more than 20 tweets come by between refreshes, you'll have an invisible [Gap in the Timeline] (http://dinesharjani.com/post/48561548010/week-3-gap-technology-part-1).

From the looks of it, the [Twitter4j library] (http://twitter4j.org/en/index.html) handles most of all the work you'd want to do with Twitter, so in essence the hardest part is actually logging in and have a safe means of saving the OAuthToken information; in this sample, Android's Shared Preferences are used to do so, but I strongly advise against this practice in a "real" application.

## Instructions

There are a couple of things you need to go through to get this code working for you.

* First: Download the project and make sure it properly compiles using the included libraries. 

* Second: The project will not fully compile, because there's an important step missing: you need to go over to [Twitter and create/register your own app] (https://dev.twitter.com/), so you can come back to the source and type in your app's Token keys in the Strings resource file.

## Other stuff

Keep in mind this code is for sample purposes. Even so, I made use of Fragments and split the code into sub-packages just like I do in production to make it all tidy and it's well documented. As I've mentioned above and specially throughout the source doe, there are things missing or which can be improved. If I haven't done so yet and you'd like to help, please feel free to do so and send a Pull Request.

## Acknowledgements

* [Twitter4j Library] (twittertimelinesample-android-holo.png)
* [Universal Image Loader for Android] (https://github.com/nostra13/Android-Universal-Image-Loader)

## Contact
Dinesh Harjani
* Email: [goldrunner18725@gmail.com] (mailto:goldrunner18725@gmail.com)
* Twitter: [@dinesharjani] (https://twitter.com/dinesharjani)

## License

Copyright 2011-2013 Sergey Tarasevich

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Copyright 2013 Dinesh Harjani

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.
