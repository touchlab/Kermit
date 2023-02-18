import React, {useContext, useEffect, useState} from 'react';
import Translate from '@docusaurus/Translate';
import {ThemeClassNames} from '@docusaurus/theme-common';
import {DocDoneContext} from "../Layout";

export default function EditThisPage({editUrl}) {
    const docDoneContext = useContext(DocDoneContext);
    const [formUrl, setFormUrl] = useState("")
    useEffect(() => {
        const username = docDoneContext.userInfo ? docDoneContext.userInfo.userDetails : "(unknown)"
        setFormUrl(`https://form.typeform.com/to/TPtgf1Zd#sourceurl=${encodeURIComponent(window.location.href)}&username=${encodeURIComponent(username)}`)
    }, [docDoneContext.userInfo])

    return (
        <a
            href={formUrl}
            target="_blank"
            rel="noreferrer noopener"
            className={ThemeClassNames.common.editThisPage}>
            <svg className="mr-2" xmlns="http://www.w3.org/2000/svg" height="16" width="16" viewBox="0 0 16 16">
                <title>megaphone</title>
                <g className="fill-cyan-500 nc-icon-wrapper">
                    <path
                        d="M3.5,4C1.6,4,0,5.6,0,7.5S1.6,11,3.5,11h0.1l2.6,4.3c0.4,0.7,1.3,0.9,2.1,0.5c0.7-0.4,0.9-1.3,0.5-2.1 L7.1,11H9l7,4V0L9,4H3.5z"></path>
                </g>
            </svg>
            <Translate
                id="theme.common.editThisPage"
                description="The link label to edit the current page">
                Give Touchlab Feedback on this page
            </Translate>
        </a>
    );
}
