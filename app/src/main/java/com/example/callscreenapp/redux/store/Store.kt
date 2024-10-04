package com.example.callscreenapp.redux.store

import com.example.callscreenapp.redux.reducer.appReducer
import com.example.callscreenapp.redux.state.AppState
import org.reduxkotlin.createThreadSafeStore

val store = createThreadSafeStore(appReducer, AppState())