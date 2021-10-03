import axios from "axios";
import store from "@/store/index";

export default function createHttp(secured = true) {

    if (secured) {
        let ting = axios.create({
            headers: {
                "Authorization": localStorage.getItem('jwt'),
            }
        });
        // Add a response interceptor, to handle status code 401
        ting.interceptors.response.use(function (response) {
            return response;
        }, function (error) {
            if(error.response.status == 401){
                //Logs out when a token no longer is valid
                store.dispatch("logout");
            }
            return Promise.reject(error);
        });

        return ting;
    } else {
        let ting2 = axios.create();

        return ting2;
    }
}
