import React from 'react';

// https://commons.wikimedia.org/wiki/File:Frog_on_a_log.jpg
import HeroImage from '@site/static/img/frog.jpg';

function HeroAbout() {
  return (
    <section className="relative">

      {/* Background image */}
      <div className="absolute inset-0">
              <img className="w-full h-full object-cover" src={HeroImage} width="1440" height="394" alt="About" />
        <div className="absolute inset-0 bg-lime-900 opacity-60" aria-hidden="true"></div>
      </div>

      {/* Hero content */}
      <div className="max-w-6xl mx-auto px-4 sm:px-6 relative">
        <div className="pt-32 pb-12 md:pt-40 md:pb-20">
          <div className="max-w-6xl mx-auto text-center">
            <h1 className="h1 mb-4 drop-shadow-lg text-white" data-aos="fade-up">Kermit - Kotlin Multiplatform Logging</h1>
            <p className="text-xl text-gray-300 mb-8 drop-shadow-lg" data-aos="fade-up" data-aos-delay="200">Kermit The Log</p>
              <div className="max-w-xs mx-auto sm:max-w-none sm:flex sm:justify-center">
                  <div data-aos="fade-up" data-aos-delay="400">
                      <a className="btn text-white bg-lime-600 hover:bg-cyan-700 w-full mb-4 sm:w-auto sm:mb-0 drop-shadow-lg" href="intro">Get Started</a>
                  </div>
                  <div data-aos="fade-up" data-aos-delay="600">
                      <a className="btn text-white bg-gray-700 hover:bg-gray-800 w-full sm:w-auto sm:ml-4 drop-shadow-lg" href="https://github.com/touchlab/Kermit">Open Github</a>
                  </div>
              </div>
          </div>
        </div>
      </div>

    </section>
  );
}

export default HeroAbout;