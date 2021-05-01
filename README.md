# My Kotlin Scripts

This is a repo that just contains Kotlin scripts I ma(d|k)e.

* `dadump`: Dumper for various comics where artists use some form of a "Next" link somewhere
* `images2pdf`: Turns images into a PDF file. Primarily intended for use after `dadump`.

For more information on Kotlin's scripting system, please refer to the [KEEP document on them](https://github.com/Kotlin/KEEP/blob/master/proposals/scripting-support.md) and its [various examples](https://github.com/Kotlin/kotlin-script-examples).

**The scripts here are not actively maintained.** They are mostly here because I wanted to host them somewhere and put them out to the world in case they would interest anyone else. They aren't particularly great code pieces either.

Licensed under the EUPL-1.2, see the [LICENSE](LICENSE) file for the English version, or check out the license [in other languages](https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12). TL;DR: AGPL but not viral. [Wikipedia has a pretty good summary of this](https://en.wikipedia.org/wiki/European_Union_Public_Licence#:~:text=another%20specificity%20of%20the%20eupl%20is%20that%20it%20is%20interoperable%2C%20without%20any%20viral%20effect%20in%20case%20of%20static%20and%20dynamic%20linking.).

## Running scripts

Running scripts requires the Kotlin compiler and an installed Java version (Java 11+ required, check out [AdoptOpenJDK](https://adoptopenjdk.net/)).

```
$ kotlin the_script_name.main.kts the args here
```

On Windows, you can install the Kotlin compiler using [Scoop](https://scoop.sh)

```
$ scoop install kotlin
```

On Windows, if commands require globbing, you can use `( gci the/gl/*.ob | % FullName )` in PowerShell, as globbing is not done automatically otherwise.

## WARNING!

You can safely ignore all of the `WARNING: An illegal...` messages (see [here](https://youtrack.jetbrains.com/issue/KT-43520) and all related dependencies).