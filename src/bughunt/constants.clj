(ns bughunt.constants)

(def BUG_STATUSES
  ["New", "Incomplete", "Invalid",
   "Won't Fix", "Confirmed", "Triaged",
   "In Progress", "Fix Committed",
   "Fix Released",
   "Opinion", "Expired"])

;; date_left_closed - when task was last reopened.
;; date_left_new - when task was marked with a status higher than New.
(def BUG_DATE_FIELDS
  [:date_assigned :date_closed :date_confirmed :date_created
   :date_fix_committed :date_fix_released :date_in_progress
   :date_incomplete :date_left_closed :date_left_new :date_triaged])
