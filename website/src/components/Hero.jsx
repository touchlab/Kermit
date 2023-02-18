import React from 'react';

import HeroImage from '@site/static/kmmpro/hero.jpg';

export default function Hero() {
    return (
        <section className="relative">

            {/* Background image */}
            <div className="absolute inset-0">
                <img className="w-full h-full object-cover" src={HeroImage} width="1440" height="394" alt="About" />
                <div className="absolute inset-0 bg-indigo-800 opacity-75" aria-hidden="true"></div>
            </div>

            {/* Hero content */}
            <div className="max-w-6xl mx-auto px-4 sm:px-6 relative">
                <div className="pt-32 pb-12 md:pt-40 md:pb-20">
                    <div className="max-w-3xl mx-auto text-center">
                        <h1 className="h1 mb-4 drop-shadow-lg text-white" data-aos="fade-up">KMM Pro</h1>
                        <p className="text-xl text-slate-300 mb-8 drop-shadow-lg mb-8" data-aos="fade-up" data-aos-delay="200">State-of-the-art KMM tools, training, and support from the industry experts</p>
                        <div className="max-w-xs mx-auto sm:max-w-none sm:flex sm:justify-center">
                            <div data-aos="fade-up" data-aos-delay="400">
                                <a className="btn text-gray-50 dark:text-gray-50 bg-indigo-600 hover:bg-indigo-700 w-full mb-4 sm:w-auto sm:mb-0 drop-shadow-lg" href="/intro">Open Docs</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </section>
    );
}
