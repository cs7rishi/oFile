import React from 'react'
import { Routes, Route } from "react-router-dom"
import { Authentication, Landing, Dashboard } from "../pages"

export const AllRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Landing />} />
      <Route path="/login" element={<Authentication />} />
      <Route path="/dashboard" element={<Dashboard />} />
    </Routes>
  )
}
