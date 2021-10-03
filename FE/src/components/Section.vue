<template>
    <div id="sectionContainer" @click="select">
        <div class="fields" id="sectionName">
            <p>{{section.name}}</p>
        </div>
        <div class="fields" id="recCapacity">
            <p>{{section.suggestedAmountPeople}}</p>
        </div>
        <input
        id="checkmark"
        type="checkbox"
        name="selectSection"
        v-model="isSelected"
        >
    </div>
</template>

<script>
export default {
    props:{
        section: Object,
        pos: Number, //This current sections postion in the 'sections' array in Room.vue
        resetCheckMark: Boolean
    },
    data(){
        return{
            isSelected: false,
        }
    },
    watch:{
        isSelected: function(val){
            /*
            Used to tell the Room component that this current is selected,
            and to know which section to add for the reservation. 
            */
            this.$emit('selected', this.pos, val);
        },
        resetCheckMark: function(){
            this.isSelected = false;
        }
    },
    methods: {
        select(){
            this.isSelected = !this.isSelected;
        }
    }
}
</script>

<style scoped>
#sectionName{
    color: black;
}
#sectionContainer{
    height: 5rem;
    width: 100%;
    display: flex;
    flex-direction: row;
    border-bottom:3px solid #f2f2f2;
    border-radius: 1px;
}

#sectionContainer:hover{
    cursor: pointer;
    background-color: #e5e5e5
}

#recCapacity{
    font-weight: lighter;
}

#checkmark {
    position: relative;
    height: 20px;
    width: 20px;
    background-color: #eee;
    margin-top: 1rem;
}

</style>