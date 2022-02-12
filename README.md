# wordle-scala
A command line Wordle clone using Scala.

### Table of Contents
**[1. Introduction](#introduction)**<br>
**[2. Running Instructions](#running-instructions)**<br>
**[3. Testing](#testing)**<br>
**[4. TODO](#todo)**<br>
**[5. License](#License)**<br>

## Introduction

This repository implements [Wordle](https://powerlanguage.co.uk/wordle/) using Scala and runs on the command line.

The `words.txt` file in `./src/main/resources` is the `popular.txt` file from [this repository](https://github.com/dolph/dictionary).

## Running Instructions

```shell
sbt:wordle-scala> run
```

## Testing

To run all the tests, run the below command:

```shell
sbt:wordle-scala> test
```

## TODO
- Add details of scalafix and scalafmt
- CI
- look into Cats effect
- Refactor game logic to make more testable

## License

[![license](https://img.shields.io/github/license/mashape/apistatus.svg)](./LICENSE)

Copyright ©‎ 2022, tcooling

Released under the MIT license, see [LICENSE](./LICENSE) for details.