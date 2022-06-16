# Quinoa request control discussion demo

This project uses Quarkus, the Supersonic Subatomic Java Framework, Renarde
and Quinoa.

If you want to learn more about Quarkus, please visit its website:
https://quarkus.io/

Quinoa: https://quarkiverse.github.io/quarkiverse-docs/quarkus-quinoa/dev/

Renarde: https://quarkiverse.github.io/quarkiverse-docs/quarkus-renarde/dev/

Qute (used by Renarde): https://quarkus.io/guides/qute

For the description of the issue with this approach in the current version
of Quinoa (1.0.6 as of writing), see the discussion link and the paragraph
below.

Discussion link: https://github.com/quarkiverse/quarkus-quinoa/discussions/113

### Renarde dev mode key problem

As of writing, Renarde has a small problem with development cryptographic keys
generation and placement when using Quarkus with Gradle:
https://github.com/quarkiverse/quarkus-renarde/issues/12

If on entry to the app you get a `no protocol: dev.publicKey.pem` exception,
just run the command below in the project root directory to place the keys in
the right place:

```shell script
cp build/classes/*.pem build/classes/java/main
```

After refreshing the page, the exception should be no more. This has to be done
after every project clean.


## Purpose

This repository was created from the (ongoing) effort of creating a starter
project for the "Htmx/Webpack on Quarkus/Renarde/Quinoa" configuration (not yet
published as of writing).

A few features:
- a mixed MPA/HDA style approach (without JavaScript the app functions as
  a classic MPA-style web app, with it - as a Htmx-powered HDA-style web app);
- Quinoa integrates and manages the UI dev server when Quarkus dev server is
  running (so no need to manage it manually) - this includes running Webpack
  dev server through `npm start` and doing an initial dependencies installation
  (via `npm install` if `node_modules` doesn't exist);
- WebPack configured to include Htmx, Bootstrap, generate source maps (dev
  mode only) and purge unwanted CSS rules (via PurgeCSS analyzing HTML templates
  and JS files for rule names; production only);
- base template provides HTML fragments when queried with Htmx and full pages 
  when not (via HtmX header detection on template level);
- nice developer experience with one dev process managing two independent
  pipelines (one to Quarkus, one for the UI assets) with "watch modes" on both
  for fast content refresh.

### Motivation

The SPA boom in recent years, while accelerating development of new techniques
and solutions of web asset management and optimizing, caused significant
drawback for developing classic MPA applications, where HTML templates
are managed server-side instead of client-side. Same rule applies to HDA-style
applications, which return HTML fragments.

While the MPA/HDA side of things managed pretty well do adapt to the changes
with framework-based solutions like Mix for Laravel, or Encore for Symfony
(which endorse and manage the Node-based UI pipelines), the Java ecosystem
remained without such solutions.

With the development of Qute, Quarkus started endorsing MPA/HDA-style
development, but still lacked the ability to integrate the Node-based build
pipeline into a single, developer-friendly process. With the development
of Quinoa, such integration is now possible without problems for SPA-style
apps. For MPA/HDA-style apps, a few issues must still be resolved.


## Running

### Prerequisites

- a version of OpenJDK installed (assumed 11+) and available on the path
- a version of Node.js installed (LTS will do) and available on the path

### Starting dev mode

Although, this should be handled when Quinoa doesn't detect the `node_modules`
directory (it runs `npm install` in dev mode if the directory is not present),
if for some reason it's not the case, go to the `src/main/webui` directory
and install the needed dependencies:
```shell script
npm install
```

You can run your application in dev mode that enables live coding using:
```shell script
./gradlew quarkusDev
```

## The request control issue

**Important note**: the issue is only related for the development process
(while running the dev mode) and is not a problem for final application builds. 

The issue is simple to recreate. After launching the app in dev mode just go
to http://localhost:8080. Upon running, instead of seeing "Page 1", you'll see
the placeholder index file from the UI dev server (used only for Quinoa to know
that the UI dev server is up - otherwise it times out during launch). By
default, you should see "Page 1", to which the app index redirects upon entry.

Quinoa provides the `ignored-path-prefixes` option which can disable redirects
to the UI dev server for supplied prefixes. The problem with however, is that
in order to fix the issue above one needs to add the index route (`/`). This
gets treated as a prefix and disables redirects to the UI dev server altogether
(every redirected request path starts with `/`, so all of them are ignored).

Effectively, setting the above option, prevents dev mode from downloading web
assets (JS, CSS, fonts, image, etc.).

### Potential solution

A number of solutions is possible:
- another option with a reversed logic: request prefixes allowed to be
  redirected (might be problematic for UI apps that "pollute" the root
  context with multiple files);
- changing `ignored-path-prefixes` to a list of regular expressions instead of
  a list of strings, so one has more control over what gets redirected.
