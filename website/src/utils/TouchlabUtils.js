
export default function configGtagUserId(){
    azureUserDetails().then(userId => {
        if(userId) {
            console.log(`userId: ${userId}`)
            window.gtag('set', {'user_id': userId})
        }
    })
}

async function azureUserDetails() {
    try {
        const response = await fetch(`/.auth/me`, {
            cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
            headers: {
                // 'code': SERVER_CODE
                // 'Content-Type': 'application/x-www-form-urlencoded',
            },
        })

        const userInfo = await response.json()

        return userInfo?.clientPrincipal?.userDetails
    } catch (e) {
        console.log(e)
        return null
    }
}
