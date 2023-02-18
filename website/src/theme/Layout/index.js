import React, {useEffect, useState} from 'react';
import Layout from '@theme-original/Layout';

export const DocDoneContext = React.createContext({donePathMap: {}, refreshDonePath: () => {}});

let IS_PROD = process.env.NODE_ENV !== "development";
const SERVER_URL = IS_PROD ? "https://api.touchlab.dev" : "http://localhost:5003"

async function getUserInfo() {
    if (IS_PROD) {
        const response = await fetch('/.auth/me');
        const payload = await response.json();
        return payload
    } else {
        return {
            clientPrincipal: {identityProvider: "testdev", userDetails: "heyo", userId: "1235"}
        }
    }
}

async function docDoneCheck(u) {
    const reqPath = `${SERVER_URL}/kmmpro/docCompletedForUser?identityProvider=${encodeURIComponent(u.identityProvider)}&userDetails=${encodeURIComponent(u.userDetails)}&userId=${encodeURIComponent(u.userId)}`
    const response = await fetch(reqPath, {
        cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
    })

    let paths = await response.json()

    let pathMap = {}
    for(const p of paths){
        pathMap[p] = "true"
    }

    return pathMap
}

export const STATE_LOADING = 0
export const STATE_LOADED = 1
export const STATE_FAILED = 2

export default function LayoutWrapper(props) {
    const [dataStatus, setDataStatus] = useState(STATE_LOADING);
    const [userInfo, setUserInfo] = useState(null);
    const [donePathMap, setDonePathMap] = useState({});

    const refreshDonePaths = () => {
        docDoneCheck(userInfo).then((result) => {
            setDonePathMap(result)
            setDataStatus(STATE_LOADED)
        }).catch(()=>{setDataStatus(STATE_FAILED)})
    }

    useEffect(() => {
        getUserInfo().then((payload) => {
            if (payload.clientPrincipal) {
                setUserInfo(payload.clientPrincipal)
            }
        })
            .catch(()=>{setDataStatus(STATE_FAILED)})
    }, []);

    useEffect(() => {
        if (userInfo) {
            refreshDonePaths()
        }
    }, [userInfo])

    const toggleDonePath = (path) => {
        if(donePathMap[path]){
            delete donePathMap[path]
        }else {
            donePathMap[path] = "true"
        }

        setDonePathMap({...donePathMap})
    }

    return (
        <>
            <DocDoneContext.Provider value={{userInfo: userInfo, donePathMap: donePathMap, dataStatus:dataStatus, refreshDonePath: refreshDonePaths, toggleDonePath: toggleDonePath}}>
                <Layout {...props} />
            </DocDoneContext.Provider>
        </>
    );
}
