import React, {useContext, useEffect, useState} from 'react';
import {DocDoneContext, STATE_LOADED} from "../../../theme/Layout";
import Banner from "./Banner";
import {useDoc} from '@docusaurus/theme-common/internal';

let IS_PROD = process.env.NODE_ENV !== "development";
const SERVER_URL = IS_PROD ? "https://api.touchlab.dev" : "http://localhost:5003"
export default function DocDone(props) {
    const [docPath, setDocPath] = useState(null);
    const docDoneContext = useContext(DocDoneContext);
    const docDone = docPath && docDoneContext.donePathMap[docPath] === "true"
    const disableDocDoneInput = docDoneContext.dataStatus !== STATE_LOADED
    const [bannerErrorOpen, setBannerErrorOpen] = useState(false);
    const [bannerLoadFailedOpen, setBannerLoadFailedOpen] = useState(false);

    const {metadata} = useDoc();

    console.log("doc.metadata", metadata)

    useEffect(() => {
        setDocPath(window.location.pathname)
    }, []);

    const showFailedLoadBanner = () => {
        if (disableDocDoneInput) {
            setBannerLoadFailedOpen(true)
        }
    }

    const toggleDocDone = () => {
        if(docDoneContext.userInfo  && docPath) {
            docDoneContext.toggleDonePath(docPath)
            fetch(`${SERVER_URL}/kmmpro/docCompleted`, {
                method: docDone ? "delete" : "post",
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },

                //make sure to serialize your JSON body
                body: JSON.stringify({
                    identityProvider: docDoneContext.userInfo.identityProvider,
                    userDetails: docDoneContext.userInfo.userDetails,
                    userId: docDoneContext.userInfo.userId,
                    path: docPath
                })
            })
                .then((response) => {
                    docDoneContext.refreshDonePath()
                })
                .catch(() => {
                    setBannerErrorOpen(true)
                    docDoneContext.toggleDonePath(docPath)
                })
            ;
        }
        return false
    }

    return (
        <section>
            <Banner className={'fixed top-0 left-0 w-full banner-extra'} type="error" open={bannerErrorOpen} setOpen={setBannerErrorOpen}>
                Could not save value. Call failed. This could be a network or server issue. Please try again later.
            </Banner>
            <Banner className={'fixed top-0 left-0 w-full banner-extra'} type="warning" open={bannerLoadFailedOpen} setOpen={setBannerLoadFailedOpen}>
                Could not load info from server. Input disabled. Refresh to try again.
            </Banner>
            <div className="max-w-6xl mx-auto px-4 sm:px-6">

                <div className="flex justify-end my-8" data-aos="fade-up" data-aos-delay="400">
                    <div className={`inline-flex items-center${disableDocDoneInput?" opacity-50":""}`} onClick={showFailedLoadBanner}>
                        <div className="text-2xl font-medium mr-3">Lesson Complete</div>
                        <div className="form-switch">
                            <input type="checkbox" disabled={disableDocDoneInput} name="pricing-toggle" id="pricing-toggle" className="sr-only" checked={docDone} onChange={toggleDocDone} />
                            <label className="bg-gray-600" htmlFor="pricing-toggle">
                                <span className="bg-gray-200" aria-hidden="true"></span>
                                <span className="sr-only">Toggle Lesson Complete</span>
                            </label>
                        </div>
                    </div>
                </div>

            </div>


        </section>
    );
}
