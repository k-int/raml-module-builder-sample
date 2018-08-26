# Sample RAML Module Builder Module

Copyright (C) 2016-2018 The Open Library Foundation

This software is distributed under the terms of the Apache License,
Version 2.0. See the file "[LICENSE](LICENSE)" for more information.

## Dependencies

### Optional

* [folio-tools](https://github.com/folio-org/folio-tools) - checked out as a peer

## Common Tasks

### Generating Code Coverage Report

To run the tests and generate a code coverage report, use:

`mvn org.jacoco:jacoco-maven-plugin:prepare-agent test`

### Validating Interface Definitions

First, checkout [folio-tools](https://github.com/folio-org/folio-tools) 
and follow the instructions in the [readme](https://github.com/folio-org/folio-tools/blob/master/lint-raml/README.md).

Then run [`./validate-interfaces.sh`](validate-interfaces.sh) to validate the interface(s) defined in the [ramls](ramls) directory 
based upon the local config [api.yml](api.yml). 
