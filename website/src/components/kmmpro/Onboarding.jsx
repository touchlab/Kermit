import React from 'react';

function FeatureLine(name){
    return (
        <li className="flex items-center">
            <svg className="w-3 h-3 fill-current text-emerald-500 mr-2 shrink-0" viewBox="0 0 12 12" xmlns="http://www.w3.org/2000/svg">
                <path d="M10.28 2.28L3.989 8.575 1.695 6.28A1 1 0 00.28 7.695l3 3a1 1 0 001.414 0l7-7A1 1 0 0010.28 2.28z" />
            </svg>
            <span>{name}</span>
        </li>
    )
}
export default function Onboarding() {
    return (
        <section>
            <div className="max-w-6xl mx-auto px-4 sm:px-6">
                {/* Section header */}
                <div className="max-w-4xl mx-auto text-center pb-12 md:pb-20">
                    <h2 className="h2 mb-4">Onboarding and Training Support</h2>
                    <div className="text-xl text-gray-700 dark:text-gray-400 mb-4">
                        All program teams start with an intensive onboarding phase. This includes access to the training
                        materials and scheduled office hours with Touchlab KMM experts to review topics and ask questions.
                        Topics specific to your project can be discussed to help get your team started on the right foot.
                        Training support also includes Toolbox product demos and training.
                    </div>
                    <h3 className="h3 mb-4 dark:text-emerald-200 text-emerald-800">Services Overview</h3>
                    <div className="md:columns-2">
                        <ul className="text-lg text-gray-700 dark:text-gray-400 -mb-2">
                            {FeatureLine("Something something")}
                            {FeatureLine("Something something")}
                            {FeatureLine("Something something")}
                            {FeatureLine("Something something")}
                            {FeatureLine("Todo: List services")}
                        </ul>
                    </div>

                </div>

            </div>
        </section>
    );
}
