import React from 'react';
import {Link} from 'react-router-dom';
import Layout from '@theme/Layout';
import PageIllustration from '../components/PageIllustration';

function PageNotFound() {
    return (
        <Layout
            title={`Not Found`}
            description="">
            <div className="preflight-wrapper">

                {/*  Page illustration */}
                <div className="relative max-w-6xl mx-auto h-0 pointer-events-none" aria-hidden="true">
                    <PageIllustration/>
                </div>

                <section className="relative">
                    <div className="max-w-6xl mx-auto px-4 sm:px-6">
                        <div className="pt-32 pb-12 md:pt-40 md:pb-20">
                            <div className="max-w-6xl mx-auto text-center">
                                {/* Top image */}
                                <div className="relative inline-flex flex-col mb-6" data-aos="fade-up">
                                    <img className="rounded-full" src="/img/404.jpg" width="196" height="196"
                                         alt="404"/>
                                    <svg className="w-20 h-20 fill-current text-teal-600 absolute top-0 right-0 -mr-6"
                                         width="84" height="80" xmlns="http://www.w3.org/2000/svg">
                                        <path
                                            d="M80 45.876l-3.434-2.142c-.313-.196-.636-.374-.95-.566l.09-.385c-.217.02-.433.034-.649.053a113.732 113.732 0 00-29.876-12.614l26.527-11.208 7.776-3.285-8.445-.716a283.299 283.299 0 00-56.645.881c-9.887 1.17-14.58 3.26-14.388 4.343.202 1.165 5.204 1.342 14.918.216a295.374 295.374 0 0146.206-1.615l-22.516 9.815-6.55 2.855 7.001 1.372c10.347 2.03 20.402 5.58 29.777 10.452a380.058 380.058 0 01-38.52.822l-5.717-.169 4.521 3.469c5.697 4.371 11.358 8.727 16.633 13.466 4.051 3.638 7.87 7.52 11.232 11.74a100.788 100.788 0 00-21.646-4.049c-2.938-.211-4.42.091-4.428.405-.01.339 1.433.688 4.317.94a98.562 98.562 0 0123.591 5.116c.36.492.722.984 1.069 1.487l.673-.864c.181.066.364.126.545.194l.653.246-.388-.584c-.107-.16-.221-.315-.329-.475l.68-.871c-.556-.198-1.116-.378-1.674-.566-3.728-5.23-8.137-9.96-12.834-14.337-4.72-4.4-9.732-8.465-14.761-12.482 13.89.587 27.83.417 41.73-.533.203.12.411.23.613.35l.094-.396c.366-.025.733-.045 1.1-.072L80 45.876z"
                                            className="fill-teal-600" fillRule="evenodd"/>
                                    </svg>
                                </div>
                                {/* 404 content */}
                                <h1 className="h1 mb-4" data-aos="fade-up" data-aos-delay="200">You don't have access to that page.</h1>
                                <p className="text-lg text-gray-400" data-aos="fade-up" data-aos-delay="400">You may need to refresh your login. Please <Link to="/.auth/logout">log out</Link> and
                                    log back in.</p>
                            </div>
                        </div>
                    </div>
                </section>

            </div>
        </Layout>
    );
}

export default PageNotFound;