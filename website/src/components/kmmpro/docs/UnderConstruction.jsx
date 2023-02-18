import React, {useEffect, useState} from 'react';
import BrowserOnly from '@docusaurus/BrowserOnly';

function caution() {
    return (
        <svg xmlns="http://www.w3.org/2000/svg" height="32" width="32" viewBox="0 0 32 32" className="mb-4">
            <title>construction
                sign</title>
            <g className="fill-white nc-icon-wrapper">
                <path className="fill-orange-500"
                      d="M31,4H1A1,1,0,0,0,0,5V17a1,1,0,0,0,1,1H31a1,1,0,0,0,1-1V5A1,1,0,0,0,31,4Zm-1,6L26,6h4ZM8,6,18,16H12L2,6ZM2,12l4,4H2Zm22,4L14,6h6L30,16Z"
                      data-color="color-2"></path>
                <rect x="4" y="1" width="3" height="3"></rect>
                <polygon points="28 29 28 20 25 20 25 29 22 29 22 31 31 31 31 29 28 29"></polygon>
                <polygon points="7 20 4 20 4 29 1 29 1 31 10 31 10 29 7 29 7 20"></polygon>
                <rect x="25" y="1" width="3" height="3"></rect>
            </g>
        </svg>
    )
}

export default function UnderConstruction() {
    const [formUrl, setFormUrl] = useState("")
    useEffect(()=>{
        setFormUrl(`https://form.typeform.com/to/th4tAzSw#sourceurl=${encodeURIComponent(window.location.href)}`)
    },[])

    return (
        <section>
            <div className="max-w-3xl mx-auto text-center content-center px-8 py-6 bg-gray-700">
                <div className="flex flex-row items-center">
                    <div className="flex-1"></div>
                    {caution()}
                    <h2 className="h2 text-white mx-4">Under Construction</h2>
                    {caution()}
                    <div className="flex-1"></div>
                </div>
                <div className="text-white text-lg">
                    This document is in our pipeline but hasn't been completed yet. If you'd like this info, please let us know and we'll put it on the top of the list.
                </div>
                <a
                    className="btn text-white hover:text-white bg-amber-600 hover:bg-orange-500 shadow text-xl mt-4"
                    href={formUrl}>Request</a>
            </div>
        </section>
    );
}
