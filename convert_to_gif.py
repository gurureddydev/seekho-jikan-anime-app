#!/usr/bin/env python3
"""
Convert MP4 video to GIF for GitHub README
Requires: pip install imageio imageio-ffmpeg pillow
"""

import sys
import os

try:
    import imageio
    from PIL import Image
except ImportError:
    print("Installing required packages...")
    os.system("pip3 install imageio imageio-ffmpeg pillow")
    import imageio
    from PIL import Image

def convert_mp4_to_gif(input_path, output_path, fps=10, scale=0.5):
    """
    Convert MP4 to GIF
    
    Args:
        input_path: Path to input MP4 file
        output_path: Path to output GIF file
        fps: Frames per second (lower = smaller file)
        scale: Scale factor (0.5 = 50% size, lower = smaller file)
    """
    print(f"Converting {input_path} to {output_path}...")
    print("This may take a few minutes...")
    
    # Read video
    reader = imageio.get_reader(input_path)
    fps_original = reader.get_meta_data()['fps']
    
    # Calculate frame skip to achieve target fps
    frame_skip = max(1, int(fps_original / fps))
    
    # Get video dimensions
    first_frame = reader.get_data(0)
    height, width = first_frame.shape[:2]
    new_width = int(width * scale)
    new_height = int(height * scale)
    
    print(f"Original: {width}x{height} @ {fps_original}fps")
    print(f"Target: {new_width}x{new_height} @ {fps}fps")
    
    # Write GIF
    writer = imageio.get_writer(
        output_path,
        fps=fps,
        codec='gif',
        quantizer='nq',  # NeuQuant quantization for better quality
        palettesize=256
    )
    
    frame_count = 0
    for i, frame in enumerate(reader):
        if i % frame_skip == 0:  # Skip frames to reduce fps
            # Resize frame
            img = Image.fromarray(frame)
            img = img.resize((new_width, new_height), Image.Resampling.LANCZOS)
            writer.append_data(img)
            frame_count += 1
            if frame_count % 30 == 0:
                print(f"Processed {frame_count} frames...")
    
    reader.close()
    writer.close()
    
    file_size = os.path.getsize(output_path) / (1024 * 1024)  # MB
    print(f"\n✅ Conversion complete!")
    print(f"Output: {output_path}")
    print(f"Size: {file_size:.2f} MB")
    print(f"Frames: {frame_count}")

if __name__ == "__main__":
    input_file = "demo/screen_recording.mp4"
    output_file = "demo/app_demo.gif"
    
    if not os.path.exists(input_file):
        print(f"Error: {input_file} not found!")
        sys.exit(1)
    
    # Convert with optimized settings for GitHub (smaller file size)
    # Lower fps and scale for smaller file size
    convert_mp4_to_gif(input_file, output_file, fps=6, scale=0.5)
    print("\n💡 Tip: If the GIF is too large, try:")
    print("   - Lower fps: fps=6")
    print("   - Smaller scale: scale=0.4")
    print("   - Or use online tools: https://ezgif.com/video-to-gif")

