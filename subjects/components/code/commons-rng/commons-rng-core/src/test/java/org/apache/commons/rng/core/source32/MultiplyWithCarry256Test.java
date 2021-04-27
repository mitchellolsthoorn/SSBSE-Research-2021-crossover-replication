/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.rng.core.source32;

import org.apache.commons.rng.core.RandomAssert;
import org.junit.Test;

public class MultiplyWithCarry256Test {
    /** The size of the array seed. */
    private static final int SEED_SIZE = 257;

    @Test
    public void testMarsaglia() {
        final int[] seed = {
            0x000587c4, // initial carry
            0xff710353, 0x1b427020, 0xc9c59991, 0x96e511e0, 0xf1d06013, 0xe0216c68, 0x98999e3d, 0xce158f68,
            0xb8d320ef, 0x905ddbf0, 0xda9717c9, 0x78498c30, 0x4681a0ab, 0x781347a8, 0x62eafcb5, 0x0a9bdc68,
            0x6229d227, 0x74066600, 0x8bed09c1, 0xed6e6c00, 0xdd94abc3, 0x250f7008, 0x035bdded, 0x156c4108,
            0x0f74e6ff, 0xa6921870, 0x81da1839, 0x7779efb0, 0xfecbd75b, 0x5af100c8, 0xa40fd3e5, 0x02a06008,
            0x310f4db7, 0xaabaa6e0, 0xfb7c2331, 0x73b1aa60, 0x7ce8d2f3, 0x0217d868, 0x4f98355d, 0x435302e8,
            0x1f47130f, 0x3e1c62f0, 0x69ac8aa9, 0xd8da50b0, 0xebe8414b, 0x39059928, 0x497a4c55, 0x049ae8e8,
            0x4b0e81c7, 0xdb024540, 0x05aed2a1, 0xd29e4e40, 0x8d4aad23, 0xcf594e48, 0x30490d8d, 0xc36989c8,
            0x6104809f, 0x97a6af70, 0xcd2f1b19, 0x0bf3d7b0, 0x0099c97b, 0xbe921008, 0xda423c85, 0x3604eec8,
            0x92516757, 0xfdefd9e0, 0xe8adc411, 0x6313a060, 0x7fa3e4d3, 0x28314068, 0xa0f0807d, 0xd0a9d668,
            0x008045af, 0xc25245f0, 0x2691e189, 0x9ff794b0, 0x25869f6b, 0xd347d828, 0xd4666475, 0x74179e68,
            0x69c57a67, 0x1ccfc380, 0x7ac37381, 0xa9325780, 0x6ab03683, 0xfcaeeb88, 0xb892adad, 0x62b41888,
            0xf28a1fbf, 0x27122d70, 0x16c8a0f9, 0xb6543630, 0x01cb9c1b, 0x052baec8, 0x277db425, 0x19cb1c88,
            0x96061ff7, 0x9d030ae0, 0x10cbd8f1, 0x6a334960, 0x0501d233, 0x1b7f7568, 0xff5da59d, 0x5eadf9e8,
            0x26ec97cf, 0xe208d2f0, 0xe934e169, 0xb7bd6930, 0xc1dc400b, 0xdc45f6a8, 0xe89ab515, 0x710932e8,
            0xcac46e07, 0x44b63440, 0xc8e42c61, 0xde16e540, 0x96e107e3, 0x565bfd48, 0x5738cf4d, 0x70a49bc8,
            0x85136f5f, 0xaab3e670, 0x4eb1efd9, 0xbc5bb630, 0x2f30423b, 0x4a1e8f88, 0xe8d40ec5, 0xbe50f9c8,
            0xe7284f97, 0x15c447e0, 0xf2c741d1, 0x30f93360, 0xc04c1c13, 0xcbf14d68, 0xcfe94abd, 0x31a9d268,
            0x7e17986f, 0x5e877ff0, 0xb1998449, 0xf7f8ed30, 0xdca7b22b, 0xf55427a8, 0xe153b735, 0xc2530968,
            0x0586f4a7, 0xdb458d00, 0xb11dbd41, 0xc6fb9b00, 0x6fd5e143, 0x61b28708, 0xd013196d, 0x10b75a08,
            0x00f40c7f, 0x40db3370, 0x126992b9, 0x793f35b0, 0xdb6d62db, 0x255836c8, 0x19d12e65, 0x3821a508,
            0x9d468137, 0x373a0ce0, 0xe81672b1, 0x9c18e560, 0x56f7fe73, 0xfb2c4768, 0x05a599dd, 0x461c6ee8,
            0x1f93d48f, 0x5ac9a8f0, 0x0a82c329, 0x0d28c0b0, 0xbfae26cb, 0x47dba628, 0xf6ec80d5, 0xdf6548e8,
            0x67487247, 0xa1110e40, 0x718f7621, 0xbed4b940, 0xc8d2b3a3, 0x25d50d48, 0x29e6110d, 0x5ac1a1c8,
            0x1aca0b1f, 0xb968e470, 0x3e051999, 0x4edb95b0, 0x9de098fb, 0x757ff708, 0x39054205, 0xfc9190c8,
            0x3594dad7, 0xc5ffd9e0, 0x9baeeb91, 0xd5285f60, 0xab207853, 0xe4805f68, 0x50a9befd, 0x7d6f7768,
            0xf8fec52f, 0xc8bdf5f0, 0xbeb6c609, 0xc00f94b0, 0xb13438eb, 0x911af728, 0x99d4b2f5, 0x5f7b7f68,
            0xe27118e7, 0x8f6a1780, 0x07f36701, 0xfd3f3f80, 0xc88fce03, 0x4c7fcb88, 0x0038712d, 0xc7b12488,
            0xd88e773f, 0xac980870, 0x57124b79, 0x87709b30, 0xbc032b9b, 0x2d2356c8, 0x9050c0a5, 0xcb59de88,
            0xba506177, 0x0b157ee0, 0x2f9ebc71, 0x09974060, 0xb308d7b3, 0xfbfa7c68, 0x587a6c1d, 0xea8397e8,
            0x6b187d4f, 0xdcbbfef0, 0xb42825e9, 0x8a4b3230, 0x1f528b8b, 0x41e03ea8, 0xa8d73195, 0x5e1b94e8,
            0x7e990087, 0x92526740, 0xd1747fe1, 0xbb8a2c40, 0xd062bc63, 0x4eec1048, 0xaa5c52cd, 0x5875e7c8,
            0x11ace7df, 0x919ee970, 0x0f2e2259, 0x55c10b30, 0xd60ae1bb, 0x2ab27788, 0xcda47a45, 0x667acdc8,
        };

        /*
         * Data generated from code snippet provided here:
         *   http://school.anhb.uwa.edu.au/personalpages/kwessen/shared/Marsaglia03.html
         */
        final int[] expectedSequence = {
            0x257c6890, 0x2a638c7d, 0x24e92547, 0x3f021e24, 0x59cd3f37, 0x92c71967, 0x6d55aab2, 0xa662f2ad,
            0x8633e15a, 0x19d38d42, 0xbbe8381e, 0x8312f976, 0xee4b2e57, 0xbe88c5d5, 0xb291b54e, 0x531ab614,
            0xb6627311, 0x8ca090fd, 0x27e62e4a, 0x57b6879b, 0xce50ecc1, 0x41cf8cc8, 0x5a12c57a, 0xf0d9051d,
            0x6cd3bdda, 0xa41671c6, 0x642bd28c, 0x1cce0718, 0x31dfe0b6, 0xd3733244, 0xa1c1b352, 0x54d4408b,
            0xa8f4eba7, 0x02e1397c, 0xeac4aad4, 0x46a63278, 0x98d27010, 0x4036a1f1, 0xe902f641, 0x26b14aa6,
            0xaeafe31c, 0x66a327ad, 0x3683346f, 0x661f8efe, 0x997da2b7, 0x5f6bb2e9, 0x0d5b7307, 0x5a70c604,
            0xef321a16, 0xdd3260c6, 0xd23a8c99, 0xbaf20620, 0xeda3454a, 0x80e40950, 0xaf7dca32, 0xc80c8c58,
            0x513bf771, 0xb8754759, 0xf41dbf72, 0x3f8c1c68, 0x26a6e60e, 0x7044afb5, 0xda8b1683, 0xee1b21dd,
            0x814c5f27, 0xcc0dd2ea, 0x61da4c4c, 0x5851b922, 0x3d042fee, 0xb4840125, 0x294f93e7, 0x086be5e9,
            0x5aa1c3fb, 0x9fa95c59, 0xf6b2daba, 0xb1c9204d, 0x4baf6c0d, 0x9cc31c15, 0x2257fb1b, 0xd6a9c727,
            0x2c71d6b1, 0x6fd0d553, 0x30d58655, 0x5370f15f, 0xb926edc9, 0x8835266c, 0x389d4d27, 0xe763f62e,
            0x0b97f8a9, 0x3a731321, 0xf8c581b7, 0x71b7c8bf, 0x3904345a, 0x1bcaad61, 0xcc3abf3f, 0x91a26d23,
            0x1f8e89ee, 0x8833e2c1, 0x04c11554, 0x39136f32, 0xd2b9856c, 0x9d00cc24, 0x4d760374, 0xc65ae148,
            0xad1a9df0, 0xde1dd1c7, 0xdadb106c, 0x7b081b19, 0x6e65ee75, 0x009c5298, 0xb8b82940, 0xd6181fc3,
            0x3692c0b2, 0xa03d1e1f, 0xb35a1df5, 0x784bdfc0, 0xa431fe7f, 0x00c6d1ad, 0x7ef0f3ef, 0x68945b04,
            0x8dcc4d02, 0x2d334d53, 0xe7751bb6, 0x911f4622, 0x703e8049, 0xed071832, 0xc4c0b6f2, 0x9449ba2d,
            0x6537639f, 0x47d7b18e, 0x92a6650f, 0x8d42c84b, 0x4f2ce769, 0xe1d0247c, 0xc7fba06b, 0x198c07d9,
            0xb81fd816, 0xcab28386, 0x17a2b884, 0xdbf55e34, 0x134179f7, 0x0088523b, 0x808fd3b8, 0x5736a030,
            0x061abf09, 0xdffd5db5, 0x2858f517, 0x2f3709b5, 0xc56e87f0, 0xa29aa85b, 0xb64befc8, 0x89649761,
            0x3b5d0058, 0xb026a416, 0xe9deb08c, 0x4c99a0fb, 0xee323b43, 0x3234a297, 0xfc7d8a82, 0xb5941989,
            0x4a61ddbf, 0xadcac556, 0x8b7a2731, 0x5b12c8ad, 0xb3b327c5, 0x88cb626c, 0xc14ad815, 0xf3054fd7,
            0x0faf1cbc, 0x3b1db6d4, 0x01e6e03e, 0x58c214ce, 0x35819aee, 0x238aa2aa, 0x62d94d21, 0xb0ba531e,
            0xc2e4d6f2, 0x6f01d233, 0xd82461e2, 0x1ccfd5b2, 0xa516f619, 0x717ca8f1, 0x16c2200a, 0x8694cb14,
            0x39212bc5, 0x76a261ba, 0xbe6cafdf, 0x1ab30291, 0x61aeca24, 0xff55bc4c, 0x4d71915c, 0x2ad797e7,
            0x83e01a94, 0xc2d3ac81, 0x47a0ea0e, 0xa8137f15, 0x48e12d0e, 0xe654097d, 0x436bee53, 0xc8dfe9b3,
            0xe7b51a7c, 0xdba4ea69, 0x0e5d71ca, 0xaad571d6, 0xe8f3f5c4, 0x60c3fb0b, 0x72bac665, 0x844e1404,
            0x516839de, 0x7039fd09, 0xa089303c, 0xc42555ab, 0xc02120b8, 0x31d9a671, 0x7da2f597, 0x0047e938,
            0xf3365c4a, 0x8a2c6c60, 0xff6e5799, 0x2c5f7a74, 0x9d3cfe7b, 0xc8c57f3d, 0x1d3473f6, 0xe75bcc43,
            0xdd33ba4f, 0x90668ad4, 0xf67ef996, 0x300fe9a4, 0x584059ef, 0x8fbe8104, 0x3e259db6, 0x9df0b7fb,
            0x52746195, 0x6a417ef5, 0x3ff9ad21, 0x8102516f, 0xda054624, 0x2477abf6, 0x1d8e5443, 0xdaaa5259,
            0x07d015e3, 0x27d68c1b, 0x930689a9, 0x075ba657, 0x915f2f8a, 0xc1911283, 0x391af175, 0xfcd2a3bc,
            0xe566bb6a, 0x5f6b4191, 0xd4b80bd2, 0x686cc681, 0x46eb852b, 0x8a6cba82, 0x873d26fe, 0x7fd36f7b,
            0x638c020e, 0xe547311d, 0x280e508d, 0x88c9cc68, 0xc9cb9d3a, 0x50256a56, 0xd7a2e984, 0x1322e7d2,
            0x016952e5, 0xadaee9e3, 0x040ad740, 0xe7b210ae, 0x659e8e59, 0x6cf0b4be, 0x367c7bf5, 0xa98bb1f8,
            0xaf8492cd, 0x793b43cc, 0xc2255740, 0xdb3efd85, 0x351b4ade, 0xa3386b57, 0xa53856e2, 0xb0a74272,
            0xffde7652, 0x3bcf325d, 0xc12b43d6, 0xaf84cfcb, 0x6bdac7e8, 0x15ff5fe2, 0x724c469d, 0xe19896ea,
            0x43cd6aa6, 0x2b7260db, 0x041c1d52, 0xd5ec2c45, 0xa1e12fc9, 0x6be494a0, 0xc64f6ef0, 0x26eea794,
            0xdfe7e36c, 0xc7d53723, 0xaca00dd0, 0x4affdf5d, 0xbd9f0df6, 0x75382815, 0x25c57824, 0x31ba4039,
            0x6c75480c, 0xa0fa2bfa, 0xbba48f53, 0x09db90e9, 0x1dc1dc19, 0xe4663531, 0xdf3d2ed7, 0x6f54e741,
            0xe6082c01, 0x2baabe7e, 0x67b2086c, 0x95d8ec2b, 0x7b4d87af, 0x43e3aae5, 0x1787dfba, 0xcc2eb97a,
            0x3428f0df, 0x1e406776, 0x485ac033, 0xccee0de0, 0xa7010382, 0x0f96c5ed, 0x8c80257b, 0x0d983da2,
            0xa7fcb482, 0xe5d0f841, 0x72b4b4fe, 0x18e06be1, 0xd98f371d, 0x040f8016, 0x7ee5b27f, 0x189be27f,
            0x179ddbba, 0x107b6e08, 0x3ea4ee95, 0x54d8d162, 0xff8b29fe, 0x562d0834, 0x87f37d17, 0x88391d07,
            0x5812f518, 0xf34be33f, 0xa9c4f645, 0x1c89446b, 0xf2111482, 0xf9ee61f8, 0x129603f8, 0x3e2e086a,
            0xecd0a435, 0x9840d54c, 0xf9664d4b, 0xce6730f8, 0xbc9ce06b, 0x0aa150d7, 0x5bbe5e90, 0xe2564486,
            0x66105cce, 0x7f38b7f3, 0xe63d1130, 0x62ac16ee, 0x77c57e00, 0x3b0226a1, 0x72ec3aea, 0x36019afc,
            0xc645ff0d, 0x974f4880, 0x91da1212, 0xe370db8b, 0xadcf17e4, 0x944ab002, 0x5b0d716c, 0xbc01086d,
            0x27211776, 0x652a32d4, 0x16be38e1, 0x6f2ea0bd, 0xd8858d93, 0x216a458c, 0x78bae9e0, 0xad14557e,
            0xbba176e8, 0x45c0d59d, 0xa6ec2833, 0x9a72408d, 0x68582f7b, 0x1631f9f8, 0x215abad0, 0x17be213c,
            0x05746888, 0x9779a0df, 0x0d9d28cf, 0x1d253cd7, 0xd06c9962, 0x00a84316, 0xc7a7ebce, 0xa4f1cb0f,
            0x08028d29, 0xaebf1b50, 0x5782537b, 0xd4d7e06b, 0x846524e3, 0x9b82ab20, 0x49288ba5, 0x58734936,
            0xc17aa021, 0xa8fd173d, 0xc20fbc6e, 0x8bbd9a5a, 0x7da87b4e, 0xafc23110, 0xdc727d0a, 0x5ede2442,
            0xfd185051, 0x18dc56ec, 0x4a0a814c, 0x906f8016, 0xda39909b, 0xd664ebf5, 0x9e58b3fd, 0x09769997,
            0x4656559e, 0x25980db9, 0x99ad5e86, 0xdda9fff3, 0x4b3b5c51, 0x85f75ff2, 0xe2712e34, 0x2d3dcaa7,
            0xcdbe3226, 0x8b709d9a, 0x6f6a35cf, 0x5dada053, 0xc81e3023, 0xa2d20dc8, 0xedb116c2, 0xaca74c89,
            0xeedeb314, 0xb865935f, 0xb9e99d2a, 0x23b1517b, 0x97cdd346, 0x4a14886e, 0x28195367, 0xaeed6a06,
            0x902e40ec, 0x4f9d50a4, 0xfcad4859, 0x8ec8d46b, 0x950f385d, 0xae293d56, 0x8eba6d86, 0xf4c7b9f0,
            0xa7e44d23, 0x340f66d9, 0xe04962b1, 0x3a6cb3b1, 0x51f0f544, 0x2102ade1, 0xdb72bac5, 0x7d754363,
            0x9c3d6466, 0xceaa6494, 0x55431cfc, 0xc6905603, 0x6f0726ba, 0xb08a516e, 0x1c501439, 0x1323c172,
            0x2bcdef90, 0xf8bf1c92, 0x4b6db4b4, 0x3d4e76f2, 0x823060d9, 0x80886870, 0x2e44e197, 0xb0f6a034,
            0x409d98d2, 0x52f8c0de, 0xd30cdebf, 0x179757c5, 0xf5bb5dad, 0x49c600bb, 0x712f9419, 0xe5aa8c01,
            0x6694ccc6, 0x6cad53a2, 0x4db659ae, 0x41d7b9c5, 0x26f5c897, 0xfe3df467, 0x8ed323bf, 0xbf8127b2,
            0x81d40377, 0x00e447b3, 0xf1ca1e7d, 0xb4ae053c, 0xd815cd65, 0xd522eaf4, 0x0d300f47, 0x23204bf1,
            0x3e86d3ed, 0x269c4485, 0x18bae574, 0x63f7157f, 0xeb56b507, 0x0e4e9f1d, 0xc162a93e, 0xa3f08b8b,
            0xb595246c, 0xd02701fe, 0x13a054c5, 0x2cd29d84, 0x66fc748d, 0x23029126, 0x022bd7bc, 0xff04526e,
            0xac70e3d3, 0x60dfbb69, 0x6beffb09, 0xae396478, 0x23507c03, 0x6d508bbd, 0xdd4b9a03, 0xefd11007,
            0xde7026de, 0x68129e59, 0x0ab76704, 0x16f918de, 0xacc2b388, 0xf5f666fc, 0x1b519c64, 0x2cc36ca7,
            0xe9dc07d6, 0x4a670a68, 0x6c797bb1, 0x813b77d8, 0x0decd048, 0x6fe1157d, 0x6725a946, 0xb131c2dc,
            0x0dc96308, 0x4ed6b9d7, 0xc3cd7a9f, 0x886b41b6, 0x3d886073, 0x414b66d2, 0x0c8db580, 0x6e11a51e,
            0x647dfd2e, 0x57df7d76, 0x4d679969, 0x36a61424, 0x34b45092, 0x6f0aa0a3, 0xfe52c2e4, 0x10aa6aee,
            0xb1bb73ab, 0x30ddbfde, 0x22898b9d, 0x21ba51b2, 0x927e526c, 0xe1a0a535, 0xd0355862, 0xb296051b,
            0x2258e82a, 0x2f017185, 0x4e037f71, 0x455e9f7b, 0x70be2aaa, 0x14f7dd2d, 0x8fe5a857, 0x888251af,
            0x41887950, 0xb1cd8c85, 0xcf88077a, 0x658834ed, 0x6de3ae38, 0xb49fa2b4, 0xbba5c792, 0xf382f47f,
            0xa829d21f, 0x35f31d13, 0xc645243f, 0xde1ee296, 0x20cb92b5, 0xcf66d510, 0x05e80309, 0x5f9f1ed0,
            0x0b5b7f0e, 0x6a53938f, 0x5e0abcb5, 0x73ff6593, 0x004d4dca, 0x39997c7b, 0x23a78298, 0xc7c08cb3,
            0xff1ee291, 0xea40f316, 0x69132b3e, 0x164b6b39, 0x0ebc2fb1, 0xe5adc964, 0xb234bed6, 0xc8bb950c,
            0xd4cbdbfc, 0xdcc3f925, 0x99772a38, 0xe7cc4d82, 0xaa5747a6, 0x7bf358a9, 0x32f37e61, 0xb1ee2868,
            0x1b432ed6, 0x8458ebc1, 0xbf116582, 0x3715233e, 0xf3917de9, 0x0e96120f, 0x105ed566, 0x359abd45,
            0x8e892c78, 0x6ea63f81, 0xce1e1d0d, 0x89732bc3, 0xdb135bf7, 0x07622b9a, 0x467b58f1, 0x71ec958f,
            0xe2e235d6, 0x7bbb202e, 0x9c854a05, 0x0f89d960, 0xbd09bcd1, 0x482a48cf, 0xb05afb69, 0x913bf292,
            0x489b5bd6, 0x8d68812b, 0xf8a3720a, 0x0c87aef4, 0x76823918, 0x05e3a35a, 0xcf0486e7, 0x53c02103,
            0xde9dd286, 0xf54f6fdb, 0xefa7877b, 0xb4661141, 0xf9d2581c, 0x4bbca497, 0xcbd2d029, 0xfab736a2,
            0x7f90c973, 0x64d0e7cc, 0xb1882fa3, 0xec5b23eb, 0xf388dbb6, 0x352b7f43, 0x4a0021b3, 0xb25f8a02,
            0xccc29b12, 0xa3573f70, 0x13c86bd1, 0xb9a1ea5b, 0x04bbff46, 0x234ca740, 0x64d36324, 0xc1c7b698,
            0xd0b3b0c0, 0x27470071, 0x86b6c003, 0x55daab1d, 0x3ae11383, 0x9ddde0b4, 0x4cbfa4e3, 0xc45b229f,
            0xe62999ce, 0xcf388be2, 0x76cf1184, 0x016c2c30, 0xafd34574, 0xb98a148f, 0x05277b87, 0x5562a329,
            0x366be4b5, 0xcaa8ac90, 0x23339579, 0x31ad226a, 0x8a72f170, 0x3945e5e9, 0x34238130, 0xf5647741,
            0x74b2be0a, 0x3168c90d, 0x1f7e75b1, 0x3001deaf, 0xf23a0992, 0x10ac859d, 0xc980d2af, 0xbffeda39,
            0x7254660b, 0xb0fa2a45, 0xa72b1d49, 0x06919c1f, 0x33ae4774, 0x3b35137a, 0xb13998d9, 0x08df0a6a,
            0x3e09ef58, 0xe1c0da12, 0x004520c8, 0x0e3e1a83, 0x75be6d84, 0xdef828b4, 0x02ba1195, 0xc0723e50,
            0x2b576c84, 0x32a75f93, 0xc787fa9d, 0x072eaeac, 0x4c28c3fe, 0x0c96e328, 0xc32ac0a4, 0x5b39b458,
            0x1362cf03, 0x42b7d8e3, 0x95098036, 0xcbdd6d82, 0x4b177650, 0x7b170cec, 0xf009e03b, 0x1d6b3ce6,
            0x598bc6ed, 0x6d83457e, 0x9b9d7632, 0x371eb318, 0x0a0b504b, 0x8a5fc450, 0x342cddb7, 0x27abdd43,
            0x1e5619a1, 0xe1b0d98a, 0x060538a1, 0x7e4c3af6, 0x88327248, 0x3973708b, 0x98461e31, 0xe986a6b0,
            0x5773b681, 0xb62f0d41, 0x6700f403, 0xa4b1d0e1, 0x80b4d98b, 0x2c4132ee, 0xa658d3b3, 0xba1b770e,
            0x60904cfd, 0xc445a139, 0xdb0ef27a, 0x4ace46cf, 0x2f8ac09e, 0x9dd71d29, 0x0fed2a2c, 0x29df377a,
            0x79d096da, 0xd5b3c519, 0xcc6b5755, 0xd4b4d312, 0x811fb101, 0xcae0d28a, 0x674d1b64, 0x19e985cf,
            0xcb2d22e6, 0x2785cedc, 0x518f9239, 0x1a66e5a1, 0xc25a529c, 0x8ddf8919, 0x557e2d1a, 0x601958fa,
            0x2cec958b, 0x2c4a1996, 0xceebe93a, 0x15583c2f, 0x16915c85, 0x38625bb7, 0xc40c45b6, 0x695f4ca6,
            0x3300a3c0, 0x5a10cc45, 0x1285f17e, 0x86d60d0e, 0x61894067, 0x98ef1dd5, 0x66d410df, 0x25896c66,
            0xf33d63ce, 0x9c28db0b, 0x8f50b049, 0x3d5fbf07, 0x898c4ab2, 0x16ecf2b9, 0x391cc660, 0xc83caf84,
            0xf839ad80, 0xab64b874, 0x220839a9, 0x43aa4e89, 0xbd89da32, 0xa43b21ed, 0x1c01bfb6, 0xbcda7042,
            0x00beab05, 0x93fa2926, 0xf7f38a29, 0xcba3e372, 0x3646218d, 0xbede27dd, 0xe5ee44a1, 0x01e0eb66,
            0x9982c956, 0x08f74ba4, 0x908297b2, 0x7b8c6ad9, 0x2ff6d71b, 0x5593404d, 0xcdf14fe5, 0x3ea33f7c,
            0x2640eb50, 0xcb2fcd6f, 0x4a2586b1, 0x63f225e8, 0x1a106462, 0xc7e0f101, 0xb86af8bc, 0x78f359e2,
            0x784f946c, 0x90add600, 0x24c61b14, 0x6445fd9d, 0xcd0e503e, 0x2b28f79d, 0x51b832bb, 0xb42e3783,
            0x62b5de9f, 0x150cfa4c, 0x30d9624d, 0x7b722b66, 0xd2f64f21, 0x9ab71b02, 0x4973968d, 0xac292292,
            0x7752d5e3, 0x5ae44d00, 0x453fb388, 0x582db40a, 0x4e70c468, 0x87d5b7cf, 0x6fba662f, 0x5fc7b4c7,
            0xec7e3a56, 0x75bd65f5, 0xcddcd8bd, 0xb7cee4ca, 0x9f808f92, 0x6eef126b, 0x87e5710f, 0x7b27b3c3,
            0x1d8b7f60, 0x36e849f8, 0xbfc46814, 0x5ab00b3b, 0x12e30b30, 0x67b46983, 0x6cbe95ea, 0x498b8bcf,
            0x553caec8, 0x48dd6ee0, 0xd4896ff7, 0x42242b53, 0xa5346f85, 0xabbd2373, 0xc00c2318, 0xc6f283f3,
            0x89fd13d3, 0x5a4d8bfc, 0x94125136, 0xedeacd7d, 0x741b5462, 0x1b40489f, 0x25aceb45, 0xdf0538c4,
            0x58af3fa8, 0x78118eaa, 0xc15e746f, 0xe890d26f, 0x826645b6, 0x315f7310, 0xf0ce725b, 0x837f8cd0,
            0xaf55bdd8, 0xa7f19807, 0x605b6830, 0x7393a6c7, 0x1726f278, 0xc9f01a15, 0x3e6d7040, 0x5ce86985,
            0xeb0ac581, 0xaea9aee0, 0x52fc0e08, 0x7ec22c7e, 0x5173aedd, 0x6add6fca, 0x0ac5043b, 0xa5ff3c54,
            0x907c747c, 0xd9ada8bb, 0xf2fa68f9, 0x1521e21b, 0x4e32b234, 0xf96826ce, 0x4c89a35f, 0x628be219,
            0x9dfeb826, 0xb5b8475f, 0x74dec36b, 0x8a3e92ba, 0xf619f494, 0xe3ffe1d6, 0x5ad677ff, 0x3a2678d2,
            0x5f562dba, 0xd7509de3, 0xea134b0d, 0x16df70e0, 0xa5de499f, 0xf61461bd, 0xbadcb3c5, 0xef72eaaa,
            0xc22faf5c, 0x248875f7, 0x250f026a, 0xcf929df9, 0xc2374a6a, 0xfc4ec03d, 0x38d46c25, 0x0a3d64ea,
            0x6d82727b, 0x1c15f45a, 0x88cd0860, 0x424ef845, 0x953ad237, 0xf6d9487c, 0x83d1c69a, 0x9e1133d5,
            0x553bd331, 0x168bdfac, 0x307d610b, 0x813c7fab, 0xbb9f9a78, 0x80c2d8a9, 0xad830f8f, 0x75c24862,
            0xb29f399c, 0xee3a4a5d, 0x031a7f0d, 0x10727cb0, 0x08a85558, 0x5f94fb2b, 0xc064ee4d, 0x88ba1026,
            0xcc3366b7, 0x76ecfdb2, 0x78ad049a, 0xcdb88483, 0x4419b7b7, 0x5706ae34, 0xcb3b2f6d, 0x0182cbaf,
            0x9f52c655, 0x6d1029da, 0xe7bf2f32, 0xfb8dbe3a, 0x55e30359, 0x5534ed84, 0x690b0d1c, 0xeada16ac,
            0xc1f43f00, 0x07af318c, 0xb5d2f6a3, 0xf3786fa8, 0x3f086f0a, 0xef283067, 0xb9a86d48, 0x6c619d98,
        };

        RandomAssert.assertEquals(expectedSequence, new MultiplyWithCarry256(seed));
    }

    @Test
    public void testConstructorWithZeroSeedIsNonFunctional() {
        RandomAssert.assertNextIntZeroOutput(new MultiplyWithCarry256(new int[SEED_SIZE]), 2 * SEED_SIZE);
    }

    @Test
    public void testConstructorWithSingleBitSeedIsFunctional() {
        RandomAssert.assertIntArrayConstructorWithSingleBitSeedIsFunctional(MultiplyWithCarry256.class, SEED_SIZE);
    }
}
