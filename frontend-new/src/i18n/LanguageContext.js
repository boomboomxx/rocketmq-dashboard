/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, {createContext, useContext, useEffect, useState} from 'react';
import {translations} from '../i18n';

const LanguageContext = createContext({
    lang: 'en',
    locale: loadLocale('en'),
    setLang: () => {
    },
    t: translations['en'], // 当前语言的文本资源
});

async function loadLocale(lang) {
    let antdLocale;
    switch (lang) {
        case 'en':
            antdLocale = await import('antd/es/locale/en_US');
            await import('dayjs/locale/en');
            break;
        case 'zh':
            antdLocale = await import('antd/es/locale/zh_CN');
            await import('dayjs/locale/zh-cn');
            break;
        // 其他
        default:
            // 默认英语
            antdLocale = await import('antd/es/locale/en_US');
            await import('dayjs/locale/en');
    }
    return antdLocale.default;
}

export const LanguageProvider = ({children}) => {
    const [lang,setLang] = useState(window.localStorage.getItem("lang") || 'en');
    const [locale,setLocale] = useState(loadLocale(lang));
    const t = translations[lang] || translations['en'];
    useEffect( () => {
        window.localStorage.setItem("lang", lang);
        loadLocale(lang).then(l=>{
            setLocale(l);
        });
    }, [lang]);
    return (
        <LanguageContext.Provider value={{lang, locale, setLang, t}}>
            {children}
        </LanguageContext.Provider>
    );
};

export const useLanguage = () => useContext(LanguageContext);

