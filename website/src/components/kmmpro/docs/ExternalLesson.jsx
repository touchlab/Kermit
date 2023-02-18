import React from 'react';

const checkMark = (color) => {
    return (
        <svg className={`w-3 h-3 fill-current text-sky-400 m-2 shrink-0" viewBox="0 0 12 12`}
             xmlns="http://www.w3.org/2000/svg">
            <path d="M10.28 2.28L3.989 8.575 1.695 6.28A1 1 0 00.28 7.695l3 3a1 1 0 001.414 0l7-7A1 1 0 0010.28 2.28z"/>
        </svg>
    )
}

function hand() {
    return (
        <svg xmlns="http://www.w3.org/2000/svg"
             className="fill-none mb-4" height="32" width="32" viewBox="0 0 32 32"><title>tool
            hand</title>
            <g className="nc-icon-wrapper fill-white">
                <path
                    d="M29.5,3A1.5,1.5,0,0,0,28,4.5V14a1,1,0,0,1-1,1h0a1,1,0,0,1-1-1V3.107a2.074,2.074,0,0,0-1.664-2.08A2,2,0,0,0,22,3V14a1,1,0,0,1-1,1h0a1,1,0,0,1-1-1V2.107A2.074,2.074,0,0,0,18.336.027,2,2,0,0,0,16,2V14a1,1,0,0,1-1,1h0a1,1,0,0,1-1-1V3.107a2.074,2.074,0,0,0-1.664-2.08A2,2,0,0,0,10,3V18.586a1,1,0,0,1-1.707.707l-4.01-4.01a2.163,2.163,0,0,0-2.461-.478,2.065,2.065,0,0,0-.884,2.863L6.133,26.9A10,10,0,0,0,14.849,32H21A10,10,0,0,0,31,22V4.5A1.5,1.5,0,0,0,29.5,3Z"
                ></path>
            </g>
        </svg>
    )
}

export default function ExternalLesson({link, linkName, expectedTime, contentHighlights, goals, children}) {
    const tabTarget = `tab-${Date.now()}`

    const renderHighlight = (th) => {
        const classes = "text-white h4 mb-0"
        if (typeof th === 'string') {
            return (<h4 className={classes}>{th}</h4>)
        } else if (th.l) {
            return (<h4 className={classes}><a className="text-cyan-400" href={th.l} target={tabTarget}>{th.t}</a></h4>)
        } else {
            return (<h4 className={classes}>{th.t}</h4>)
        }
    }

    return (
        <section>
            <div className="max-w-6xl mx-auto">
                {/* CTA box */}
                <div
                    className="relative bg-gradient-to-tr from-blue-900 to-fuchsia-800 rounded px-8 py-8 overflow-hidden"
                    data-aos="zoom-out">
                    <div className="flex flex-col items-center mb-3 h3 text-white italic">
                        <div className="text-center">{linkName}</div>
                    </div>
                    <div className="px-6">
                        <div className="py-2 mt-4 border-t text-center border-b border-l-0 border-r-0 border-gray-500">
                            <a
                                className="btn text-white bg-gradient-to-t from-cyan-600 to-cyan-400 hover:to-cyan-500 group shadow-lg text-xl"
                                href={link} target={tabTarget}>Open Page
                                <svg className="ml-2" width="24" height="24" viewBox="0 0 24 24">
                                    <path className="fill-white"
                                          d="M21 13v10h-21v-19h12v2h-10v15h17v-8h2zm3-12h-10.988l4.035 4-6.977 7.07 2.828 2.828 6.977-7.07 4.125 4.172v-11z"></path>
                                </svg>
                            </a>
                        </div>
                        {expectedTime &&
                            <div className="text-lg text-white mt-4">
                                <h4 className="h4 mb-3 text-white">Expected time: <span className="font-medium">{expectedTime}</span></h4>
                            </div>
                        }
                        <div className="text-xl text-white mt-4">
                            {children}
                        </div>
                        {contentHighlights &&
                            <>
                                <h4 className="h4 mb-3 mt-4 text-white">Content Highlights:</h4>
                                    {contentHighlights.map((th) => {
                                        return (
                                            <div className="mt-2">
                                                {renderHighlight(th)}
                                                {th.d &&
                                                    <div className="text-white">
                                                        {th.d}
                                                    </div>
                                                }
                                            </div>
                                        )
                                    })}
                            </>
                        }
                        {goals &&
                            <>
                                <h4 className="h4 mb-3 mt-4 text-white">Learning goals:</h4>
                                <ul className="text-lg text-white mb-2 -ml-2">
                                    {goals.map((goal) => {
                                        return (
                                            <li className="flex items-start mb-2">
                                                {checkMark("lime")}
                                                <span>{goal}</span>
                                            </li>
                                        )
                                    })}
                                </ul>
                            </>
                        }

                    </div>
                </div>
            </div>
        </section>
    )
}
