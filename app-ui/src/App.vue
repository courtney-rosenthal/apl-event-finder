<script setup>
  import SearchMain from "./components/SearchMain.vue";
</script>

<template>
  <div>
    <h1>Austin Public Library Event Search</h1>
    <p style="margin-bottom: 10px">
      This proof of concept demo provides advanced search for the events published at <a href="https://library.austintexas.gov/events">https://library.austintexas.gov/events</a>.
      It is running on a fixed data snapshot with past events included.
      The repository for this open source project is <a href="https://github.com/courtney-rosenthal/apl-event-finder">here</a>.
      Please report any bugs you encounter <a href="https://github.com/courtney-rosenthal/apl-event-finder/issues">here</a>.
    </p>
  </div>
  <div style="clear: both"></div>
  <SearchMain />
</template>

<style scoped>
</style>
