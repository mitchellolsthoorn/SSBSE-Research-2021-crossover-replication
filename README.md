# SSBSE-Research-2021-replication

## Subjects selection
In this experiment, we find our subjects from Apache Commons components.

### Select interesting components
Since we are interested in projects which are using numbers and strings frequently, we selected the following components for this experiment:

* __components using number__
    * [Math](https://github.com/apache/commons-math) is a library of lightweight, self-contained mathematics and statistics components.
    * [Numbers](https://github.com/apache/commons-numbers) contains utilities for working with complex numbers.
    * [Geometry](https://github.com/apache/commons-geometry) provides types and utilities for geometric processing.
    * [RNG](https://github.com/apache/commons-rng) provides pure-Java implementation of pseudo-random generators.
    * [Statistics](https://github.com/apache/commons-statistics) provides tools for statistics. This project is still in the beta version. So, it can be a good option for us to test it by EvoSuite.
* __components using string__
    * [CLI](https://github.com/apache/commons-cli) provides a simple API for presenting, processing and validating a command line interface.
    * [Text](https://github.com/apache/commons-text) is a library focused on algorithms working on strings.
    * [Lang](https://github.com/apache/commons-lang) provides extra functionality for classes in java.lang.
    * [Logging](https://github.com/apache/commons-logging) is a thin adapter allowing configurable bridging to other, well known logging systems.

To clone and build these components, run the following script:

```bash
. subjects/scripts/clone-and-build.sh subjects/components/components.csv 
```
This script has only one input parameter which is a CSV file containg data about the selected components.