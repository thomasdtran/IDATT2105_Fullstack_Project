<template>
  <link
      href="//maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css"
      rel="stylesheet"
  />
  
  <div id="body">
    <div class="container">
      <div class="innerContainer">
        <h2>RoomiU & Zectionet</h2>
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
      },
      error:false,
      errorMsg:"",
      submitted:false,
    };
  },
  created() {},
  methods: {
    login() {
      this.submitted = true;
      if (this.user.email && this.user.password) {
        store.dispatch("login", this.user).then(() => {
          this.submitted = false;
          if(!store.getters.isAuthenticated){
            this.error = true;
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

button{
  max-height: 3rem;
  width: 10rem;
}
.container{
  background-color: #333a56;
  margin: 8rem auto auto;
  width: 31rem;
  text-align: center;
  border-radius: 10px;
}

/* Change the white to any color */
input:-webkit-autofill,
input:-webkit-autofill:hover, 
input:-webkit-autofill:focus, 
input:-webkit-autofill:active
{
 -webkit-box-shadow: 0 0 0 30px #333a56 inset !important;
 
}

/*Change text in autofill textbox*/
input:-webkit-autofill
{
 -webkit-text-fill-color: white !important;
}

h2{
  color: #E8E8E8;
}

.invalid-feedback{
  color: red;
  margin-bottom: 1rem;
}

.noBullet {
  list-style-type: none;
  padding: 0;
  margin: 1rem;
}

#body{
  min-height: 100vh;
}

.btn{
  margin-top: 0.6rem;
  margin-bottom: 1rem;
  color: #333a56;
  background-color: #E8E8E8;
}

.innerContainer{
  font-size: 1.5rem;
  color: #17252a;
  margin: 1rem;
  height: 100%;
}

::placeholder{
  color: #E8E8E8;
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

.notAvailable{
  opacity: 0.1;
}
.notAvailable:hover{
  cursor: auto;
}

.icon {
  color: #E8E8E8;
  min-width: 10%;
  text-align: center;
}

</style>
