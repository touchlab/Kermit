import React from 'react';

function FeatureLine(name){
    return (
        <li className="flex items-center text-left">
            <svg className="w-3 h-3 fill-current text-cyan-500 mr-2 shrink-0" viewBox="0 0 12 12" xmlns="http://www.w3.org/2000/svg">
                <path d="M10.28 2.28L3.989 8.575 1.695 6.28A1 1 0 00.28 7.695l3 3a1 1 0 001.414 0l7-7A1 1 0 0010.28 2.28z" />
            </svg>
            <span>{name}</span>
        </li>
    )
}
export default function Training() {
    return (
        <section>
            <div className="max-w-6xl mx-auto px-4 sm:px-6">
                {/* Section header */}
                <div className="max-w-4xl mx-auto text-center pb-12 md:pb-20">
                    <h2 className="h2 mb-4">Kotlin iOS Focused Training</h2>
                    <div className="text-xl text-gray-700 dark:text-gray-400 mb-4">
                        Writing some shared Kotlin code is pretty simple, but crafting a Swift-friendly API, packaged
                        and published for easy integration, optimized for production environments, is a whole different
                        and brand new skill set. Our ever-expanding training material covers important topics, unique
                        to Kotlin Multiplatform:

                    </div>
                    <h3 className="h3 mb-4 dark:text-cyan-200 text-cyan-800">Topics Covered</h3>
                    <div className="md:columns-2">
                        <ul className="text-lg text-gray-700 dark:text-gray-400 -mb-2">
                            {FeatureLine("iOS Frameworks: building and deploying")}
                            {FeatureLine("SPM vs Cocoapods")}
                            {FeatureLine("Binary details: static vs dynamic, linking issues")}
                            {FeatureLine("Binary size: measurement and optimization")}
                            {FeatureLine("Todo: Add more stuff :)")}
                        </ul>
                    </div>

                </div>

            </div>
        </section>
    );
}
