
<template>
    <transition name="fade">
  <div class="modal-backdrop">
    <div class="modal">
      <header class="modal-header">
        <slot name="header">
          This is the default title!
        </slot>
        <button
          type="button"
          class="btn-close"
          @click="close"
        >
          x
        </button>
      </header>

      <div class="modal-body">
        <slot name="body">
          This is the default body!
        </slot>
       </div>

    </div>
  </div>
    </transition>
</template>

<script>
  export default {
    name: 'Modal',
    methods: {
      close() {
        this.$emit('close');
      },
    },
  };
</script>

<style>
  .modal-backdrop {
    position: fixed;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    background-color: rgba(0, 0, 0, 0.3);
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .modal {
    background: #FFFFFF;
    box-shadow: 2px 2px 20px 1px;
    overflow-x: auto;
    display: flex;
    flex-direction: column;
    width: 58rem;
    height: 45rem;
  }

  .modal-header{
    padding: 15px;
    display: flex;
  }

  .modal-header {
    position: relative;
    border-bottom: 1px solid #eeeeee;
    justify-content: space-between;
  }
  .modal-body {
    position: relative;
    padding: 20px 10px;
    height: 100%;
  }

  .btn-close {
    position: absolute;
    top: 0;
    right: 0;
    border: none;
    font-size: 20px;
    padding: 10px;
    cursor: pointer;
    font-weight: bold;
    background: transparent;
  }
  
.fade-enter-active {
  transition: all 0.3s ease-out;
}

.fade-leave-active {
  transition: opacity 0.5s ease;
}

.fade-leave-to{
    opacity: 0;
}

.fade-enter-from{
  transform: translateY(-20px);
  opacity: 0;
}
</style>