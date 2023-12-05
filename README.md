# wordle-scala

![CI Build](https://github.com/tcooling/wordle-scala/actions/workflows/scala.yml/badge.svg)

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

The branch build runs the following command:

```shell
sbt:wordle-scala> prBuild
```

Which runs `scalafix` and `scalafmt` before running all the tests.

## TODO
- look into Cats effect
- hard mode

## License

[![license](https://img.shields.io/github/license/mashape/apistatus.svg)](./LICENSE)

Copyright ©‎ 2022, tcooling

Released under the MIT license, see [LICENSE](./LICENSE) for details.