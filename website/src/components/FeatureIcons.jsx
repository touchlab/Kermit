import React from 'react';


function CircleBlock(svgBody) {
    return (
        <svg className="w-16 h-16 mb-4" viewBox="0 0 64 64" xmlns="http://www.w3.org/2000/svg">
            <rect className="fill-current text-cyan-600" width="64" height="64" rx="32"/>
            {svgBody()}
        </svg>
    )
}

export function FavList() {
    return (
        <>{CircleBlock(() => {
            return (
                <>
                    <path className="stroke-current text-cyan-100"
                          d="M30 39.313l-4.18 2.197L27 34.628l-5-4.874 6.91-1.004L32 22.49l3.09 6.26L42 29.754l-3 2.924"
                          strokeLinecap="square" strokeWidth="2" fill="none" fillRule="evenodd"/>
                    <path className="stroke-current text-cyan-300" d="M43 42h-9M43 37h-9" strokeLinecap="square"
                          strokeWidth="2"/>
                </>
            )
        })}</>
    )
}

export function Note() {
    return (
        <>{CircleBlock(() => {
            return (
                <>
                    <path className="stroke-current text-cyan-100" strokeWidth="2" strokeLinecap="square" d="M21 23h22v18H21z" fill="none" fillRule="evenodd" />
                    <path className="stroke-current text-cyan-300" d="M26 28h12M26 32h12M26 36h5" strokeWidth="2" strokeLinecap="square" />
                </>
            )
        })}</>
    )
}

export function World2() {
    return (
        <>{CircleBlock(() => {
            return (
                <g transform="translate(21 21)" strokeLinecap="square" strokeWidth="2" fill="none" fillRule="evenodd">
                    <ellipse className="stroke-current text-cyan-300" cx="11" cy="11" rx="5.5" ry="11" />
                    <path className="stroke-current text-cyan-100" d="M11 0v22M0 11h22" />
                    <circle className="stroke-current text-cyan-100" cx="11" cy="11" r="11" />
                </g>
            )
        })}</>
    )
}

export function Cyborg() {
    return (
        <>{CircleBlock(() => {
            return (
                <g transform="translate(22 21)" strokeLinecap="square" strokeWidth="2" fill="none" fillRule="evenodd">
                    <path className="stroke-current text-cyan-100" d="M17 22v-6.3a8.97 8.97 0 003-6.569A9.1 9.1 0 0011.262 0 9 9 0 002 9v1l-2 5 2 1v4a2 2 0 002 2h4a5 5 0 005-5v-5" />
                    <circle className="stroke-current text-cyan-300" cx="13" cy="9" r="3" />
                </g>
            )
        })}</>
    )
}

export function ThumbUp() {
    return (
        <>{CircleBlock(() => {
            return (
                <g strokeLinecap="square" strokeWidth="2" fill="none" fillRule="evenodd">
                    <path className="stroke-current text-cyan-100" d="M29 42h10.229a2 2 0 001.912-1.412l2.769-9A2 2 0 0042 29h-7v-4c0-2.373-1.251-3.494-2.764-3.86a1.006 1.006 0 00-1.236.979V26l-5 6" />
                    <path className="stroke-current text-cyan-300" d="M22 30h4v12h-4z" />
                </g>
            )
        })}</>
    )
}

export function Messaging() {
    return (
        <>{CircleBlock(() => {
            return (
                <g transform="translate(21 22)" strokeLinecap="square" strokeWidth="2" fill="none" fillRule="evenodd">
                    <path className="stroke-current text-cyan-300" d="M17 2V0M19.121 2.879l1.415-1.415M20 5h2M19.121 7.121l1.415 1.415M17 8v2M14.879 7.121l-1.415 1.415M14 5h-2M14.879 2.879l-1.415-1.415" />
                    <circle className="stroke-current text-cyan-300" cx="17" cy="5" r="3" />
                    <path className="stroke-current text-cyan-100" d="M8.86 1.18C3.8 1.988 0 5.6 0 10c0 5 4.9 9 11 9a10.55 10.55 0 003.1-.4L20 21l-.6-5.2a9.125 9.125 0 001.991-2.948" />
                </g>
            )
        })}</>
    )
}

export function NetworkConnection() {
    return (
        <>{CircleBlock(() => {
            return (
                <g transform="translate(21 22)" stroke-linecap="square" stroke-width="2" fill="none" stroke-linejoin="miter"
                   className="nc-icon-wrapper" stroke-miterlimit="10">
                    <line data-cap="butt" x1="8.6" y1="10.2" x2="15.4" y2="6.8" stroke-linecap="butt"
                          className="stroke-current text-cyan-100"></line>
                    <line data-cap="butt" x1="8.6" y1="13.7" x2="15.4" y2="17.1" stroke-linecap="butt"
                          className="stroke-current text-cyan-100"></line>
                    <circle cx="5" cy="12" r="4" className="stroke-current text-cyan-300"></circle>
                    <circle cx="19" cy="5" r="4" className="stroke-current text-cyan-300"></circle>
                    <circle cx="19" cy="19" r="4" className="stroke-current text-cyan-300"></circle>
                </g>
            )
        })}</>
    )
}

export function FilterOrganization() {
    return (
        <>{CircleBlock(() => {
            return (

                <g stroke-linecap="square" stroke-width="2" fill="none" stroke-linejoin="miter"
                   className="nc-icon-wrapper" stroke-miterlimit="10">
                    <g transform="translate(20 20)">
                        <line x1="3" y1="13" x2="10" y2="13" className="stroke-current text-cyan-100"></line>
                        <line x1="3" y1="5" x2="10" y2="5" className="stroke-current text-cyan-100"></line>
                        <polyline points=" 3,1 3,21 10,21 " className="stroke-current text-cyan-100"></polyline>
                        <rect x="10" y="3" width="11" height="4" className="stroke-current text-cyan-300"></rect>
                        <rect x="10" y="19" width="11" height="4" className="stroke-current text-cyan-300"></rect>
                        <rect x="10" y="11" width="11" height="4" className="stroke-current text-cyan-300"></rect>
                    </g>
                </g>

            )
        })}</>
    )
}

export function WarningSign() {
    return (
        <>{CircleBlock(() => {
            return (
                    <g stroke-linecap="square" stroke-width="2" fill="none" className="stroke-current text-cyan-100" stroke-linejoin="miter"
                       className="nc-icon-wrapper" stroke-miterlimit="10">
                        <g transform="translate(20 19)">
                            <path
                                className="stroke-current text-cyan-100"
                                d="M1.243,18.953,10.152,2.111a2.093,2.093,0,0,1,3.7,0l8.909,16.842A2.079,2.079,0,0,1,20.908,22H3.092A2.079,2.079,0,0,1,1.243,18.953Z"></path>
                            <line x1="12" y1="8" x2="12" y2="14" className="stroke-current text-cyan-300"></line>
                            <circle cx="12" cy="17.5" r="1.5" stroke="none" className="fill-current text-cyan-300"></circle>
                        </g>
                    </g>
            )
        })}</>
    )
}

