import React from 'react';
import Layout from '@theme/Layout';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import HeroAbout from '../components/HeroAbout';
import FeaturesBlocks from '../components/FeaturesBlocks';
import TopSpacer from '../components/TopSpacer';
// import 'aos/dist/aos.css';
// import AOS from 'aos';

export default function Kmmpro(): JSX.Element {
  const {siteConfig} = useDocusaurusContext();

  // useEffect(() => {
  //   AOS.init({
  //     once: true,
  //     disable: 'phone',
  //     duration: 350,
  //     easing: 'ease-out-sine',
  //   });
  // });

  return (
      <Layout
          title={`KMM Pro`}
          description="">
        <div className="preflight-wrapper">
          <HeroAbout/>
          <FeaturesBlocks/>
        </div>
      </Layout>
  );
}
