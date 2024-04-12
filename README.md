<!---
   @author     Tim Urenda
   @creation   11 March 2024
--->

#Remote BQL Query

This is an example module that implements a couple different ways to run a remote BQL query. The first is a BComponent with an Action that establishes a Fox session from a supervisor to all stations in the Niagara Network and runs a query ORD against them. The second is a Provisioning step that can be configured to run against a selected set of remote Niagara stations. This step runs the same query ORD.

You will need to set up your certificate in order to compile and run this code, and configure the `niagaraSigning` task in the `build.gradle.kts` file.
See the developer documentation for information on how to set up your signing certificate.

You will also need to change the gradle.properties contents to point to your Niagara installation.