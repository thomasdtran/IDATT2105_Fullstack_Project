<!-- View for logging in to the admin page -->
<template>
    <link
            href="//maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css"
            rel="stylesheet"
    />
    <div id="body">
        <div class="container">
            <div class="innerContainer">
                <h3>RoomiU & Zectionet Admin</h3>
                <ul class="noBullet">
                    <li>
                        <i class="fa fa-user icon"></i>
                        <label for="email"></label>
                        <input id="email"
                               v-model="user.email"
                               type="text"
                               name="email"
                               class="inputFields"
                               placeholder="Email"
                               required
                        />
                        <div v-show="submitted && !user.email" class="invalid-feedback">
                            Skriv inn email
                        </div>
                    </li>

                    <li>
                        <i class="fa fa-lock icon"></i>
                        <label htmlFor="password"></label>
                        <input id="password"
                               v-model="user.password"
                               type="password"
                               name="password"
                               class="inputFields"
                               placeholder="Passord"
                        />
                    </li>
                </ul>
            </div>
            <div id="loginButton">
                <button v-if="submitted"
                class="btn" 
                disabled=true><Spinner/></button>

                <button v-else
                class="btn" 
                @click="login" 
                :class="{notAvailable:!user.email || !user.password}" 
                :disabled="!user.email || !user.password">Logg inn</button>
            </div>
            <div v-show="error" class="invalid-feedback">
                {{errorMsg}}
            </div>
        </div>
    </div>

</template>

<script>
    import store from "@/store/index";
    import Spinner from "@/components/Spinner.vue"
    export default {
        components:{
            Spinner,
        },
        data() {
            return {
                user: {
                    email: "",
                    password: "",
                    authError : false
                },
                submitted: false,
                error:false,
                errorMsg:"",
            };
        },
        created() {},
        methods: {
            //Method for logging admin in to admin site
            login() {
                this.submitted = true;
                if (this.user.email && this.user.password) {
                    //runs adminLogin method in "store"
                    store.dispatch("adminLogin", this.user).then(() => {
                        this.submitted = false;
                        //Sets error if admin is not authenticated
                        if(!store.getters.isAuthenticated){
                            this.error = true;
                            this.user.authError = true;
                            this.errorMsg = "Feil brukernavn eller passord";
                        }
                    })
                }
            },
        }
    };
</script>

<style scoped>
    * {
        overflow: auto;
        box-sizing: border-box;
        padding: 0;
        margin: 0;
    }
    /* Change the white to any color */
    input:-webkit-autofill,
    input:-webkit-autofill:hover, 
    input:-webkit-autofill:focus, 
    input:-webkit-autofill:active
    {
    -webkit-box-shadow: 0 0 0 30px #333a56 inset !important;
    
    }

    button{
        max-height: 3rem;
        width: 10rem;
    }

    /*Change text in autofill textbox*/
    input:-webkit-autofill
    {
    -webkit-text-fill-color: white !important;
    }
    h3 {
        margin-top: 10px;
        margin-bottom: 25px;
        color: #E8E8E8;
    }

    .container{
        background-color: #333a56;
        margin: 8rem auto auto;
        width: 31rem;
        text-align: center;
        border-radius: 10px;
    }

    .invalid-feedback{
        color: red;
        margin-bottom: 1rem;
    }

    .noBullet {
        list-style-type: none;
        padding: 0;
        margin-bottom: 2rem;
    }

    .innerContainer{
        font-size: 1.5rem;
        color: #17252a;
        margin: 1rem;
        height: 100%;
    }

    .inputFields{
        margin-top: 1rem;
        margin-bottom: 1rem;
        font-size: 16px;
        width: 14rem;
        border: 1px solid #E8E8E8;
        border-top: none;
        border-left: none;
        border-right: none;
        background: transparent;
        color: #E8E8E8;
        outline: none;
    }

    .icon {
        color: #E8E8E8;
        min-width: 10%;
        text-align: center;
    }
    #buttonContainer {
        position: relative;
        height: 50px;
        bottom: 15px;
    }
    .btn{
        margin-top: 0.2rem;
        margin-bottom: 1rem;
        color: #333a56;
        background-color: #E8E8E8;
    }

    .notAvailable{
        opacity: 0.1;
    }
    .notAvailable:hover{
        cursor: auto;
    }

</style>
