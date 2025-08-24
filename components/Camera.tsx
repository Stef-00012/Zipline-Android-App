import { styles } from "@/styles/components/camera";
import { useCallback, useRef, useState } from "react";
import { Pressable, StyleSheet, View } from "react-native"
import { type CameraProps, type CameraRuntimeError, Camera as NativeCamera, type Point/*, Templates*/, useCameraDevice/*, useCameraFormat*/ } from "react-native-vision-camera"
import Reanimated, { Extrapolation, interpolate, useAnimatedProps, useSharedValue } from 'react-native-reanimated'
import { Gesture, GestureDetector } from 'react-native-gesture-handler'
import { runOnJS } from "react-native-worklets";

interface Props {
    onError?: (error: CameraRuntimeError) => void
    microphoneEnabled?: boolean;
    disableCamera: () => void;
}

export default function Camera({
    onError,
    microphoneEnabled = false,
    disableCamera
}: Props) {
    const camera = useRef<NativeCamera>(null)
    const cameraDevice = useCameraDevice("back")
    const zoom = useSharedValue<number>(cameraDevice?.neutralZoom ?? 1)

    const [cameraMode, setCameraMode] = useState<"photo" | "video">("video");

    const [isRecording, setIsRecording] = useState(false);

    const ReanimatedCamera = Reanimated.createAnimatedComponent(NativeCamera)

    const zoomOffset = useSharedValue(0);
    const zoomGesture = Gesture.Pinch()
        .onBegin(() => {
        zoomOffset.value = zoom.value
        })
        .onUpdate(event => {
        const z = zoomOffset.value * event.scale
        zoom.value = interpolate(
            z,
            [1, 10],
            [cameraDevice?.minZoom ?? 1, cameraDevice?.maxZoom ?? 1],
            Extrapolation.CLAMP,
        )
        })

    const animatedProps = useAnimatedProps<CameraProps>(
        () => ({ zoom: zoom.value }),
        [zoom]
    )

    const focus = useCallback((point: Point) => {
        if (!camera.current || !cameraDevice) return;

        if (!cameraDevice.supportsFocus) return;
        
        camera.current.focus(point)
    }, [cameraDevice])

    const focusGesture = Gesture.Tap()
        .onEnd(({ x, y }) => {
        runOnJS(focus)({ x, y })
        })

    const gestures = Gesture.Race(zoomGesture, focusGesture)

    if (!cameraDevice) {
        disableCamera()

        return null;
    }

    return (
        <View style={styles.mainContainer}>
            <GestureDetector gesture={gestures}>
                <ReanimatedCamera
                    device={cameraDevice}
                    isActive
                    style={[
                        StyleSheet.absoluteFill,
                        {
                            zIndex: 1
                        }
                    ]}
                    photo
                    video
                    audio={microphoneEnabled}
                    onError={onError}
                    ref={camera}
                    enableFpsGraph
                    animatedProps={animatedProps}
                />
            </GestureDetector>
            
            <View style={styles.uiContainer}>
                <Pressable
                    style={{
                        ...styles.captureButton,
                        ...(cameraMode === "photo" && styles.captureButtonPhoto),
                        ...(cameraMode === "video" && styles.captureButtonVideo),
                    }}
                    onPress={async () => {
                        if (!camera.current) return;

                        if (cameraMode === "video") {
                            if (!isRecording) {
                                console.log("start rec")
                                camera.current.startRecording({
                                    onRecordingFinished: (video) => {
                                        console.log(video)
                                        setIsRecording(false)
                                    },
                                    onRecordingError: console.error,
                                })
                                
                                setIsRecording(true)
                            } else {
                                console.log("stop rec")
                                camera.current.stopRecording()
                            }
                        } else {
                            camera.current.takePhoto().then(console.log)
                        }
                    }}
                />
            </View>
        </View>
    )
}