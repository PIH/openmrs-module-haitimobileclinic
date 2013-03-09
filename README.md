openmrs-module-haitimobileclinic
================================

Lightweight data entry based on OpenMRS to support mobile clinics in Haiti.

Introduction
------------

Currently four local OpenMRS installations on four laptops provide data entry support for four static facilities through Haiti. The main purpose is to enable the mobile clinics of these static facilities to keep track of referrals from the mobile clinics (at varying locations) to the main facility as well as supporting to workflow of community-based tuberculosis testing.

A couple of additional and introductionary documents are available under https://github.com/PIH/openmrs-module-haitimobileclinic-tools/tree/master/doc and should be read beforehand.

Using sync to keep all these system up to date was considered at one point, but abandoned for system complexity. Instead remote access through Internet is envisioned, but details (logistically and technically) have not yet been defined.

haitimobileclinic overview
--------------------------

Parts of this module is forked from the patientregistration module. The Point of Care patientregistration was adapted to the needs of the mobile clinic patient registration. Additionally retrospective data entry components are implemented on top of 'vanilla OpenMRS'.
- HTML Forms
- OpenMRS patient dashboard portlet to enable 'quick access' to HTML forms
- Custom JSP pages and with some JSP tags to provide a register-like and partially editable list of patients
- OpenMRS customization (CSS, pihhaiti module)
- OpenMRS configs
- Reports

Development environment
-----------------------

A default OpenMRS development environment is required to make changes to the OpenMRS module haitimobileclinic (available under https://github.com/PIH/openmrs-module-haitimobileclinic). In Eclipse the maven-ized module behaves like other OpenMRS modules.

Current versions of OpenMRS and modules
---------------------------------------
OpenMRS 1.9.2 Build e9813c
addresshierarchy-2.2.8.omod
calculation-1.0.omod
htmlformentry-2.0.4.omod
htmlwidgets-1.6.2.omod
idgen-2.3.omod
namephonetics-1.3.2.omod
pihhaiti-1.3.6-SNAPSHOT.omod
reporting-0.7.6-SNAPSHOT-without-log4j.omod
reportingcompatibility-1.5.8.omod
serialization.xstream-0.2.7.omod
validation-1.0.1.omod

Metadata
--------

A snapshot of the metadata was taken from the zanmi production server early February 2013 in evolved from then on its own. All the required metadata to set up a new system for a mobile clinic is found in the metadata dir of the openmrs-module-haitimobileclinic-tools repository. 

The statements can be applied to an empty OpenMRS database and installation with all the required modules. For the production systems there is a backup/restore cycle, which could also be used to come to a functional OpenMRS database.

Note: Attempts to use the metadata sharing module and trying to keep in sync with the zanmi server were not successful (due to technical and time constraints). But as besides additions like new concepts also deletions/retirements of existing (not relevant) metadata happened, this 'forking' was inevitable.

Updating the local installations
--------------------------------

Most components of the EMR installation at the local computers can be remotely and semi-automatically updated. An update batch file on every system pulls changes from the repository https://github.com/PIH/openmrs-module-haitimobileclinic-tools and applies them as needed on the system. This mainly includes
- Metadata (like concepts, reports, ...)
- OpenMRS modules
- Custom SQL statements
- OpenMRS web archive