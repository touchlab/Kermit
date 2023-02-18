import React from 'react';

export default function Summary() {
    return (
        <section>
            <div className="max-w-6xl mx-auto px-4 sm:px-6">
                {/* Section header */}
                <div className="max-w-4xl mx-auto text-center pb-12 md:pb-20">
                    <h2 className="h2 mb-4">KMM Pro is how Touchlab builds with KMM</h2>
                    <div className="text-xl text-gray-700 dark:text-gray-400 mb-4">
                        Touchlab has been focused on Kotlin Multiplatform since the platform's inception. Our experts have worked
                        with every type of KMP project, and with many orgs. We have developed a specialize internal training curriculum
                        and knowledge base, along with a custom set of industry-leading tools.
                    </div>
                    <div className="text-xl text-gray-700 dark:text-gray-400 mb-4">
                        By joining KMM Pro, you can give your core KMM engineers the skills and tools to produced
                        excellent
                        production-grade shared mobile code.
                    </div>
                    <div className="font-architects-daughter dark:text-sky-500 h3 mb-4">
                        KMM Pro is a critical investment in your KMM success
                    </div>
                </div>

            </div>
        </section>
    );
}
