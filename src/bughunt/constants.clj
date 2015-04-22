(ns bughunt.constants)

(def BUG_STATUSES
  ["Confirmed"
   "Expired"
   "Fix Committed"
   "Fix Released"
   "In Progress"
   "Incomplete"
   "Invalid"
   "New"
   "Opinion"
   "Triaged"
   "Won't Fix"])

(def BUG_DATE_FIELDS
  [:date_assigned
   :date_closed
   :date_confirmed
   :date_created
   :date_fix_committed
   :date_fix_released
   :date_in_progress
   :date_incomplete
   :date_left_closed     ; date reopened
   :date_left_new        ; date marked with a status higher than New
   :date_triaged])
