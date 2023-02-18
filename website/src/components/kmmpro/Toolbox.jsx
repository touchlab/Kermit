import React from 'react';


export default function Toolbox() {
    return (
        <section>
            <div className="max-w-6xl mx-auto px-4 sm:px-6">
                {/* Section header */}
                <div className="max-w-4xl mx-auto text-center pb-12 md:pb-20">
                    <h2 className="h2 mb-4">KMM Pro Experts Toolbox</h2>
                    <div className="text-xl text-gray-700 dark:text-gray-400 mb-4">
                        Touchlab publishes some of the core open source tools used by the KMP ecosystem. We also spend
                        considerable time building tools for our team to use. For the first time, and exclusively
                        through
                        the KMM Pro program, we are makind these tools available to industry professionals.
                    </div>
                    <div className="font-architects-daughter dark:text-lime-200 text-lime-800 h3 mb-4">What's in the box?</div>

                    {/* Section header */}
                    <div className="max-w-4xl mx-auto text-center pt-12 md:pt-20 border-t border-gray-800 ">
                        <h2 className="h2 mb-4">(This needs more elaborate design)</h2>
                        {/*<div className="text-xl text-gray-700 dark:text-gray-400">
                Less complex screens can be all shared Compose code. Platform-specific code can be written directly wherever you need
                it to be, and still use the same shared Kotlin underneath. For Android, it's all "Compose", so it is always
                fully native. On iOS, it's all SwiftUI, and you can code it directly wherever you need to.

                <h3 className="h3 mt-8 text-black dark:text-white">Fully native UI with minimal platform risk.</h3>

            </div>*/}
                    </div>

                </div>

            </div>
        </section>
    );
}
