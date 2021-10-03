const utils = {
    apiUrl: process.env.VUE_APP_API_URL,

    /*
    getUser() {
        return JSON.parse(localStorage.getItem("user"));
    },


    getConfigWithHeader() {
        const headers = {
            "Content-Type": "application/json",
            Authorization: "Bearer " + this.getUser().jwt,
        };
        return { headers: headers };
    },

     */

    updateUser(user) {
        localStorage.setItem(
            "user",
            JSON.stringify({
                id: user.id,
                jwt: user.jwt,
                email: user.email,
            })
        );
    },
    logOut(){
        localStorage.clear()
    }
};

export default utils;
