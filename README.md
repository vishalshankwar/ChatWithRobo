# ChatWithRobo
An Android AI chat app built with Kotlin &amp; Jetpack Compose, running Google's Gemma 3 1B (int4-quantized) LLM entirely on-device via MediaPipe LLM Inference API.

# Gemma Chat

An Android chat application that runs **Google's Gemma 3 1B** language model completely **on-device** — no cloud API, no internet connection, no data leaving the phone. Built with **Kotlin**, **Jetpack Compose**, and Google's **MediaPipe LLM Inference API**, using an int4-quantized `.litertlm` model for efficient local inference on mobile hardware.

## Features
- 100% offline AI inference — runs entirely on-device
- Real-time streaming responses
- Clean, modern chat UI built with Jetpack Compose
- MVVM architecture with Kotlin Coroutines & StateFlow
- Custom prompt engineering for concise, context-aware replies

## Tech Stack
- Kotlin, Jetpack Compose, Material 3
- MediaPipe GenAI Tasks (`tasks-genai`)
- Gemma 3 1B IT (int4 quantized, LiteRT format)
- MVVM + Kotlin Coroutines + Flow
