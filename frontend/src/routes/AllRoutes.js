import React from 'react'
import { Routes, Route } from "react-router-dom"
import { Authentication, Landing, Dashboard } from "../pages"
import { Downloads } from '../components'

export const AllRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Landing />} />
      <Route path="/login" element={<Authentication />} />
      <Route path="/dashboard" element={<Dashboard />} />
      <Route path="/dashboard/downloads" element={<Dashboard ChildComponent={Downloads} />}/>
      <Route path="/dashboard/profile" element={<Dashboard ChildComponent={Downloads} />}/>
    </Routes>
  )
}
