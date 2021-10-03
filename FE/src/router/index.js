import { createRouter, createWebHistory} from "vue-router";
import LoginPage from "@/views/LoginPage";
import Home from "@/views/Home";
import store from "@/store/index";
import AdminLoginPage from "../views/AdminLoginPage";
import AdminPage from "../views/AdminPage";



const routes = [
    {
        path: "/login",
        name: "login",
        component: LoginPage,
        meta:{
            guest:true
        }
    },
    {
        path: "/login/admin",
        name: "admin login",
        component: AdminLoginPage,
        meta:{
            guest:true
        }
    },
    {
        path: "/admin",
        name: "admin home",
        component: AdminPage,
        meta:{
            requiresAuth:true,
            admin:true
        }
    },
    {
        path: "/",
        name: "home",
        component: Home,
        meta:{
            requiresAuth:true
        }
    }
];

const router = createRouter({
     history: createWebHistory(),
     routes,
});

router.beforeEach((to, from, next) => {
    if(to.matched.some(record => record.meta.requiresAuth)){
        if(!store.getters.isAuthenticated){
            next({name:'login'});
        }else{
            if(to.matched.some(record => record.meta.admin)){
                if(store.getters.getRole === "ADMIN"){
                    next(); 
                }else{
                    next({name:'home'}); 
                }
            }else{
                next()
            }
        }
    }else if(to.matched.some(record => record.meta.guest)){
        if(store.getters.isAuthenticated){
            if(store.getters.getRole === "ADMIN"){
                next({name:'admin home'}); 
            }else{
                next({name:'home'}); 
            }
        }else{
            next();
        }
    }
    else{
        next();
    }
})

export default router;