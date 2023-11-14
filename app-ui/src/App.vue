<script setup>
  import SearchMain from "./components/SearchMain.vue";
</script>

<template>
  <div class="info-header">
    <h1>Austin Public Library Event Search</h1>

  </div>
  <!-- <div style="clear: both"></div> -->
  <!-- bad idea to use divs like this for formatting. Better to use css. -->
  <SearchMain />
</template>

<style scoped>
.info-header {
  margin-left: 25px;
  display: block;
}
</style>
