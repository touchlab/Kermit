# Kotlin/Wasm Browser Example

A simple app showing Kermit calls directly and through a shared module.

## Build and run

Check out the repo, navigate to the project folder, and use the following command:
```
./gradlew :wasm-browser:wasmBrowserRun
```

## Setup Environment

>**Note**
> Using experimental Kotlin/Wasm may require enabling experimental features in the target environment.

- **Chrome** 110 or newer: enable **WebAssembly Garbage Collection** at [chrome://flags/#enable-webassembly-garbage-collection](chrome://flags/#enable-webassembly-garbage-collection) or with Chrome 109 or newer, run the program with the `--js-flags=--experimental-wasm-gc` command line argument.
- **Firefox Nightly** 112 or newer: enable **javascript.options.wasm_function_references** and **javascript.options.wasm_gc** at [about:config](about:config).
- **Edge** 109 or newer: run the program with the `--js-flags=--experimental-wasm-gc` command line argument.

For more information see https://kotl.in/wasm_help/.

## IDE

This sample was tested on `IntelliJ IDEA 2023.2.2 (Community Edition)`