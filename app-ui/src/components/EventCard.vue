<script setup>
defineProps(['item']);

import dayjs from "dayjs";
import Card from 'primevue/card';

// XXX - I would have thought dayjs would do day-of-week names.
const dayNames = [
    "Sun",
    "Mon",
    "Tue",
    "Wed",
    "Thu",
    "Fri",
    "Sat"
]

function formatDate(t) {
  return dayNames[t.day()] + ", " + t.format("MMM D");
}

function formatTime(time) {
  const t0 = dayjs.unix(parseInt(time.start));
  const start_date = formatDate(t0);
  const start_time = t0.format("h:mm a");
  const start = `${start_date}, ${start_time}`

  if (! time.end) {
    return start;
  }

  const t1 = dayjs.unix(parseInt(time.end));
  const end_date = formatDate(t1);
  const end_time = t1.format("h:mm a");

  if (start_date == end_date) {
    return `${start} - ${end_time}`;
  } else {
    return `${start} - ${end_date}, ${end_time}`;
  }
}
</script>

<template>
  <Card>
    <template #title>
      <a :href="item.url">{{ item.title }}</a>
    </template>
    <template #content>
      <p>{{ item.summary }}</p>
      <p>{{ item.location }}</p>
      <p>{{ formatTime(item.time) }}</p>
    </template>
  </Card>
</template>

<style scoped>
fieldset { display: block; width: 100%; clear: both; }
fieldset span { white-space: nowrap; }
label { padding-left: 5px; padding-right: 1.0em; }
.p-card div.p-card-content { padding: 0;  }  /*this isn't working*/
.clear { break-before: left; } /*this isn't working*/
</style>
