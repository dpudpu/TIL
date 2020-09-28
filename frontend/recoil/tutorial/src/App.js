import React, {useEffect} from 'react';
import {atom, RecoilRoot, selector, useRecoilState, useRecoilValue} from "recoil";

function App() {
    return (
        <RecoilRoot>
            <CharacterCounter/>
        </RecoilRoot>
    );
}

const textState = atom({
    key: 'textState',
    default: 'firstRecoil',
});

const charCountState = selector({
    key: 'charCountState',
    get: ({get}) => {
        const text = get(textState);
        return text.length;
    }
});

function CharacterCounter() {
    return (
        <div>
            <TextInput/>
            <CharacterCount/>
        </div>
    )
}

function TextInput() {
    const [text, setText] = useRecoilState(textState);

    const onChange = (event) => {
        setText(event.target.value);
    };

    return (
        <div>
            <input type="text" value={text} onChange={onChange}/>
            <br/>
            Echo: {text}
        </div>
    )
}

function CharacterCount() {
    const count = useRecoilValue(charCountState);

    return <>Character Count: {count}</>;
}

export default App;
