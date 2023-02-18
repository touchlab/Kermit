import React from 'react';
import Layout from '@theme/Layout';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Hero from '../components/kmmpro/Hero';
import Summary from '../components/kmmpro/Summary';
import Training from '../components/kmmpro/Training';
import Onboarding from '../components/kmmpro/Onboarding';
import Toolbox from '../components/kmmpro/Toolbox';
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
          <Hero/>
          <TopSpacer/>
          <Summary/>
          <Training/>
          <Onboarding/>
          <Toolbox/>
        </div>
      </Layout>
  );
}
