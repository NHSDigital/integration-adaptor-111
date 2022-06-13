const readTextFile = (file: File): Promise<string | ArrayBuffer | null> =>
  new Promise((resolve) => {
    const reader = new FileReader();
    reader.onload = (e) => {
      const text = e?.target?.result;
      resolve(text ?? null);
    };
    reader.readAsText(file);
  });

export default readTextFile;
