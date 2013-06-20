Welcome to the XIP Hosting Service&trade; Project!
==================================================

The [eXtensible Imaging Platform&trade; (XIP&trade;)](http://www.OpenXIP.org) is an
Open Source project that provides a framework for developing image processing and
visualization applications.  While XIP's emphasis is medical applications, it can be 
used for other types of applications.

The XIP Hosting Service&trade; component of XIP&trade; performs the role of a
[DICOM Hosting System] (http://medical.nema.org/Dicom/2011/11_19pu.pdf).  It 
essentially is a non-interactive version of the
[XIP Host&trade;](https://github.com/XIP/xip-host)
that runs as a web service.  Currently the XIP Hosting Service project is in its
very earliest stages, more of a proof of concept than a finished project.  It does
accept an XML work item request, from which it locates and retrieves any needed
data, and launches the specified application.  It will need much discussion and
refinement to truly become useful.  And it would lead to change proposals to the
DICOM Application Hosting specification, since hosting as a service was not one of
the use cases considered by DICOM Work Group 23, who created the specification.
Naturally, volunteers who are willing to help are encouraged to contribute.

The ultimate goals of the project are subject to discussion.

XIP&trade;, including the XIP Hosting Service&trade; is distributed under the [Apache 2.0 License](http://opensource.org/licenses/Apache-2.0).
Please see the NOTICE and LICENSE files for details.

You will find more details about XIP&trade; in the following links:

*  [Home Page] (http://www.OpenXIP.org)
*  [Forum/Mailing List] (https://groups.google.com/forum/?fromgroups#!forum/openxip)
*  [Issue tracker] (https://plans.imphub.org/browse/XIP)
*  [Documentation] (https://docs.imphub.org/display/XIP)
*  [Git code repository] (https://github.com/OpenXIP/xip-host)
