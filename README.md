# Introduction 
Paladin is a Java based implementation of the classic arcade game Pac-Man utilizing LibGdx.

# Installation

## Prerequisites

The following are required to install and run the application.
(for just running the final jar I beleive JRE 17 should be enough).

- Maven 
- Java 17

## Installation Steps

I plan on setting up a release of this application on GitHub and then providing a fat jar that way but until then you can build from source.

The following instructions explains building the app from source.

1. Clone the repository:

```sh
git clone https://github.com/imawful/paladin.git
cd paladin/
```

2. Install the app:

```sh
# ensure you are in the root directory of cloned repo
mvn install
```

3. Compile:

```sh
mvn clean compile 
```

## How to run the application

After compiling you can run the app from the root directory.

```sh
mvn exec:java
```

Or you can package the application to get the fat jar and run using java.

```sh
mvn clean package
java -jar target/paladin-0.9.jar 
```

# Features

So far the game just auto plays level one and then will quit if you lose 3 lives or win the game. As I implement more of the levels (which should be easy with
my level builder) I'll be able to do more than just force quit.

Right now I am focused on getting the games code to be more modular. I want to allow for easy extensibility. I plan on making a maze builder that can build
a maze based on a .tmx tiled map. This would allow for custom maps to be made and played easily. Furthermore I would like add some extra UI such as menus
so that you can be able to play different levels and maybe set some settings. 

[X] Maze Interface.
[X] Create Level Data Builder. (build a level based on data)
[X] 'Ready' before each session.
[X] Sound Effects. 
[X] Detect GameOver.
[X] Level One

[ ] Extra UI. (menus.. hud...)
[ ] Maze builder (from tiled file).
[ ] Numeric score.
[ ] Fruits.


# Testing

I did experiment with writing a few tests for the different ghost's AI but I haven't fully written any robust tests at the moment.

Right now there is kind of a major issue with how I'm handling the collision logic, particularly with the ghosts. When the frame rate is high (~144fps) , the ghost
will kind freeze up and not want to move. I believe it's how im using the delta value in the collisions logic but while debugging I think I also need to consider refactoring how my ghosts is choosing it's next velocity. Even still with the game locked at 60fps things behave as expected so all is well for the time being, If you feel like you want to help me debug this or help refactor you can see `Ghost.java` and `App.java` for more details. 

# Contributions

All contributions are welcome! If you have any new changes, features, or bug fixes I would be happy to take a look at the pull request and merge any 
new changes. All I ask is that you provide a somewhat detailed description of the changes to help better understand your code. You may go ahead and fork
the project, create a new branch, commit your changes, and then submit the pull request. It would be greatly appreciated if your changes align with the
projects goal of a modular system focused on easy extensibility :)

# Conclusion

Thank you for taking your time to check out my project Paladin. I hope that as I continue updating Paladin it can turn into a fun PacMan game where you can design your own levels and share them with others to play. If you have any feedback or questions/concerns related to Paladin that isn't appicable to a GitHub issue feel free to contact me directly! Thank you again.
- Kevin Barrios
