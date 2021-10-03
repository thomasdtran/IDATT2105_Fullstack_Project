import {createStore} from 'vuex'
import createHttp from "@/services/http";
import router from "@/router";

const store = createStore({
  state: {
    isBusy: false,
    error: "",
  },
  mutations: {
    setBusy: (state) => state.isBusy = true,
    clearBusy: (state) => state.isBusy = false,
    setError: (state, error) => state.error = error,
    clearError: (state) => state.error = "",
  },
  getters: {
    isAuthenticated: () => localStorage.getItem('jwt') != null,
    getToken: () => localStorage.getItem('jwt'),
    getRole: () => localStorage.getItem('role')
  },
  actions: {
    login: async ({ commit }, model) => {
      try {
        commit("setBusy");
        commit("clearError");
        const http = createHttp(false); // unsecured
        await http.post(process.env.VUE_APP_API_URL + "/api/v1/login", model)
            .then( result => {
              const token = result.headers["authorization"];
              localStorage.setItem('jwt', token);

              const httpSecure = createHttp(); //secure
              httpSecure.get(process.env.VUE_APP_API_URL + "/api/v1/role")
              .then(result =>{
                localStorage.setItem('role', result.data);
                router.push("/");
              })
            })
            .catch( err => {
              console.log("error in post");
              console.log(err);
            });
      } catch (err){
        console.log("CATCH!");
        console.log(err);
        commit("setError", "Failed to login");
      } finally {
        commit("clearBusy");
      }
    },
    adminLogin:  async ({ commit }, model) => {
      try {
        commit("setBusy");
        commit("clearError");
        const http = createHttp(false); // unsecured
        await http.post(process.env.VUE_APP_API_URL + "/api/v1/login", model)
            .then( result => {
              const token = result.headers["authorization"];
              localStorage.setItem('jwt', token);
              const httpSecure = createHttp(); //secure
              httpSecure.get(process.env.VUE_APP_API_URL + "/api/v1/role")
              .then(result =>{
                localStorage.setItem('role', result.data);
                router.push("/admin");
              })
            })
            .catch( err => {
              console.log("error in post");
              console.log(err);
            });
      } catch (err){
        console.log("CATCH!");
        console.log(err);
        commit("setError", "Failed to login");
      } finally {
        commit("clearBusy");
      }
    },
    logout: () => {
      localStorage.removeItem('jwt');
      localStorage.removeItem('role');
      router.push("/login");
    },
    adminLogout: () => {
      localStorage.removeItem('jwt');
      localStorage.removeItem('role');
      router.push("/login/admin");
    }
  }
})

export default store
